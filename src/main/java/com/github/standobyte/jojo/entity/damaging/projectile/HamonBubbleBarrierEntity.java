package com.github.standobyte.jojo.entity.damaging.projectile;

import com.github.standobyte.jojo.action.ActionTarget.TargetType;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.init.ModActions;
import com.github.standobyte.jojo.init.ModEffects;
import com.github.standobyte.jojo.init.ModEntityTypes;
import com.github.standobyte.jojo.init.ModNonStandPowers;
import com.github.standobyte.jojo.power.IPower;
import com.github.standobyte.jojo.power.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.nonstand.type.HamonPowerType;
import com.github.standobyte.jojo.power.nonstand.type.HamonSkill.HamonStat;
import com.github.standobyte.jojo.util.damage.ModDamageSources;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class HamonBubbleBarrierEntity extends ModdedProjectileEntity {
    private static final int MAX_BARRIER_TICKS = 100;
    private int barrierTicks;
    private boolean barrier;
    private boolean shot;
    private IPower<?> power;
    
    public HamonBubbleBarrierEntity(World world, LivingEntity shooter, IPower<?> power) {
        super(ModEntityTypes.HAMON_BUBBLE_BARRIER.get(), shooter, world);
        this.power = power;
    }

    public HamonBubbleBarrierEntity(EntityType<? extends HamonBubbleBarrierEntity> type, World world) {
        super(type, world);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide()) { 
            if (barrier && (barrierTicks++ >= MAX_BARRIER_TICKS || !isVehicle()) || power == null) {
                remove();
            }
            else if (!shot) {
                if (power.getHeldAction() != ModActions.CAESAR_BUBBLE_BARRIER.get()) {
                    remove();
                }
                else if (power.getHeldActionTicks() >= ModActions.CAESAR_BUBBLE_BARRIER.get().getHoldDurationToFire(power) - 1) {
                    Entity owner = getOwner();
                    shootFromRotation(owner != null ? owner : this, 1.0F, 0.0F);
                    shot = true;
                }
            }
            else if (isVehicle() && tickCount % 5 % 2 == 0) {
                ModDamageSources.dealHamonDamage(getPassengers().get(0), 0.002F, this, getOwner());
            }
        }
        else {
            Vector3d sparkVec = Vector3d.directionFromRotation(random.nextFloat() * 360F, random.nextFloat() * 360F)
                    .scale(getBbWidth() / 2).add(getX(), getY(0.5), getZ());
            HamonPowerType.createHamonSparkParticles(level, ClientUtil.getClientPlayer(), sparkVec, 0.1F);
        }
    }
    
    @Override
    public void remove() {
        super.remove();
        getPassengers().forEach(entity -> {
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).removeEffect(ModEffects.STUN.get());
            }
        });
    }
    
    @Override
    protected boolean hurtTarget(Entity target, LivingEntity owner) {
        return ModDamageSources.dealHamonDamage(target, 0.1F, this, owner);
    }

    @Override
    protected void afterEntityHit(EntityRayTraceResult entityRayTraceResult, boolean entityHurt) {
        if (entityHurt) {
            Entity target = entityRayTraceResult.getEntity();
            if (target instanceof LivingEntity && target.startRiding(this)) {
                barrier = true;
                ((LivingEntity) target).addEffect(new EffectInstance(ModEffects.STUN.get(), 100));
                setDeltaMovement(new Vector3d(0, 0.05D, 0));
            }
            LivingEntity owner = getOwner();
            if (owner != null) {
                INonStandPower.getNonStandPowerOptional(owner).ifPresent(power -> {
                    power.getTypeSpecificData(ModNonStandPowers.HAMON.get()).ifPresent(hamon -> {
                        hamon.hamonPointsFromAction(HamonStat.STRENGTH, ModActions.CAESAR_BUBBLE_BARRIER.get().getHeldTickManaCost() / 4F);
                    });
                });
            }
        }
    }
    
    @Override
    protected void breakProjectile(TargetType targetType) {
        if (targetType != TargetType.ENTITY && !isVehicle()) {
            super.breakProjectile(targetType);
        }
    }

    @Override
    public float getBaseDamage() {
        return 0;
    }

    @Override
    protected float getMaxHardnessBreakable() {
        return 0;
    }

    @Override
    public boolean standDamage() {
        return false;
    }
    
    public float getSize(float partialTick) {
        return Math.min((tickCount + partialTick) / (float) ModActions.CAESAR_BUBBLE_BARRIER.get().getHoldDurationToFire(null), 1);
    }

    @Override
    public void positionRider(Entity entity) {
       if (hasPassenger(entity)) {
           entity.setPos(getX(), getY() + (getBbHeight() - entity.getBbHeight()) / 2, getZ());
        }
    }
    
    @Override
    public double getPassengersRidingOffset() {
        return getBbHeight() / 2;
    }
    
    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        if (barrier) {
            nbt.putBoolean("Barrier", barrier);
            nbt.putInt("BarrierTicks", barrierTicks);
        }
        nbt.putBoolean("Shot", shot);
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        this.barrier = nbt.getBoolean("Barrier");
        this.barrierTicks = nbt.getInt("BarrierTicks");
        this.shot = nbt.getBoolean("Shot");
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected int ticksLifespan() {
        return barrier ? 100 : 100 + MAX_BARRIER_TICKS;
    }
}
