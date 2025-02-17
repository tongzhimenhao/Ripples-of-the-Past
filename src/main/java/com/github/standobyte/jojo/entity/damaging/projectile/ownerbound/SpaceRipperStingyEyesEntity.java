package com.github.standobyte.jojo.entity.damaging.projectile.ownerbound;

import com.github.standobyte.jojo.init.ModActions;
import com.github.standobyte.jojo.init.ModEntityTypes;
import com.github.standobyte.jojo.power.nonstand.INonStandPower;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class SpaceRipperStingyEyesEntity extends OwnerBoundProjectileEntity {
    private static final DataParameter<Float> LENGTH = EntityDataManager.defineId(SpaceRipperStingyEyesEntity.class, DataSerializers.FLOAT);
    private INonStandPower ownerPower;
    private boolean rightEye;
    private Vector3d detachedOriginPos;

    public SpaceRipperStingyEyesEntity(World world, LivingEntity owner, boolean rightEye) {
        super(ModEntityTypes.SPACE_RIPPER_STINGY_EYES.get(), owner, world);
        this.rightEye = rightEye;
    }
    
    public SpaceRipperStingyEyesEntity(EntityType<? extends SpaceRipperStingyEyesEntity> entityType, World world) {
        super(entityType, world);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (!isAlive()) {
            return;
        }
        if (!isBoundToOwner()) {
            detachedOriginPos = detachedOriginPos.add(position().subtract(xOld, yOld, zOld));
        }
        if (!level.isClientSide()) {
            if (isBoundToOwner() && (ownerPower == null || ownerPower.getHeldAction() != ModActions.VAMPIRISM_SPACE_RIPPER_STINGY_EYES.get())) {
                setBoundToOwner(false);
                setDeltaMovement(position().subtract(getOriginPoint()).normalize().scale(movementSpeed()));
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(DataParameter<?> dataParameter) {
        if (IS_BOUND_TO_OWNER.equals(dataParameter) && !isBoundToOwner() && getOwner() != null) {
            detachedOriginPos = getOriginPoint();
        }
        super.onSyncedDataUpdated(dataParameter);
    }
    
    @Override
    public void setOwner(Entity owner) {
        super.setOwner(owner);
        if (owner instanceof LivingEntity) {
            ownerPower = INonStandPower.getNonStandPowerOptional((LivingEntity) owner).orElse(null);
        }
    }
    
    private void setLength(float length) {
        entityData.set(LENGTH, length);
    }
    
    public float getLength() {
        return entityData.get(LENGTH);
    }
    
    private static final Vector3d OFFSET_LEFT_EYE = new Vector3d(0.09375, -0.2, 0.0);
    private static final Vector3d OFFSET_RIGHT_EYE = new Vector3d(-OFFSET_LEFT_EYE.x, OFFSET_LEFT_EYE.y, OFFSET_LEFT_EYE.z);
    @Override
    protected Vector3d getOwnerRelativeOffset() {
        return rightEye ? OFFSET_RIGHT_EYE : OFFSET_LEFT_EYE;
    }
    
    private static final Vector3d OFFSET_XROT = new Vector3d(0, 0.2, 0.0);
    @Override
    protected Vector3d getXRotOffset() {
        return OFFSET_XROT;
    }
    
    @Override
    public Vector3d getOriginPoint(float partialTick) {
        if (!isBoundToOwner()) {
            if (detachedOriginPos == null) {
                detachedOriginPos = super.getOriginPoint(partialTick);
            }
            return detachedOriginPos;
        }
        return super.getOriginPoint(partialTick);
    }

    @Override
    protected int ticksLifespan() {
        if (isBoundToOwner()) {
            return 50;
        }
        return MathHelper.floor(getLength() / (float) movementSpeed() * 2F) + 20;
    }
    
    @Override
    protected float movementSpeed() {
        return 0.5F + level.getDifficulty().getId() * 0.25F;
    }
    
    @Override
    protected void checkRetract() {}

    @Override
    public boolean standDamage() {
        return false;
    }
    
    @Override
    public float getBaseDamage() {
        return 1.0F + level.getDifficulty().getId();
    }
    
    @Override
    protected boolean shouldHurtThroughInvulTicks() {
        return true;
    }
    
    @Override
    protected float getMaxHardnessBreakable() {
        return 3.0F;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(LENGTH, 0F);
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putFloat("Length", getLength());
        nbt.putBoolean("IsRightEye", rightEye);
        if (detachedOriginPos != null) {
            nbt.put("DetachedOrigin", newDoubleList(detachedOriginPos.x, detachedOriginPos.y, detachedOriginPos.z));
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT nbt) {
        if (nbt.contains("DetachedOrigin", 9)) {
            ListNBT detachedPosList = nbt.getList("DetachedOrigin", 6);
            detachedOriginPos = new Vector3d(detachedPosList.getDouble(0), detachedPosList.getDouble(1), detachedPosList.getDouble(2));
        }
        super.readAdditionalSaveData(nbt);
        setLength(nbt.getFloat("Length"));
        rightEye = nbt.getBoolean("IsRightEye");
    }
    
    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        super.writeSpawnData(buffer);
        buffer.writeBoolean(rightEye);
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        super.readSpawnData(additionalData);
        rightEye = additionalData.readBoolean();
    }
}
