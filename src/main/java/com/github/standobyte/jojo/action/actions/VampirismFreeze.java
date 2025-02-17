package com.github.standobyte.jojo.action.actions;

import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.ActionTarget.TargetType;
import com.github.standobyte.jojo.init.ModActions;
import com.github.standobyte.jojo.init.ModEffects;
import com.github.standobyte.jojo.init.ModSounds;
import com.github.standobyte.jojo.power.IPower;
import com.github.standobyte.jojo.power.nonstand.INonStandPower;
import com.github.standobyte.jojo.util.damage.ModDamageSources;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class VampirismFreeze extends Action {

    public VampirismFreeze(AbstractBuilder<?> builder) {
        super(builder);
    }
    
    @Override
    public ActionConditionResult checkConditions(LivingEntity user, LivingEntity performer, IPower<?> power, ActionTarget target) {
        if (user.level.getDifficulty() == Difficulty.PEACEFUL) {
            return conditionMessage("peaceful");
        }
        if (performer.isOnFire()) {
            return conditionMessage("fire");
        }
        if (user.level.dimensionType().ultraWarm()) {
            return conditionMessage("ultrawarm");
        }
        if (!performer.getMainHandItem().isEmpty()) {
            return conditionMessage("hand");
        }
        return ActionConditionResult.POSITIVE;
    }
    
    @Override
    public void onHoldTickUser(World world, LivingEntity user, IPower<?> power, int ticksHeld, ActionTarget target, boolean requirementsFulfilled) {
        if (!world.isClientSide() && requirementsFulfilled) {
            if (target.getType() == TargetType.ENTITY) {
                Entity entityTarget = target.getEntity(world);
                if (entityTarget instanceof LivingEntity && !entityTarget.isOnFire()) {
                    int difficulty = world.getDifficulty().getId();
                    LivingEntity targetLiving = (LivingEntity) entityTarget;
                    if (ModDamageSources.dealColdDamage(targetLiving, 1.5F * difficulty, user, null)) {
                        EffectInstance freezeInstance = targetLiving.getEffect(ModEffects.FREEZE.get());
                        if (freezeInstance == null) {
                            world.playSound(null, targetLiving, ModSounds.VAMPIRE_FREEZE.get(), targetLiving.getSoundSource(), 1.0F, 1.0F);
                            targetLiving.addEffect(new EffectInstance(ModEffects.FREEZE.get(), difficulty * 30, 0));
                        }
                        else {
                            int additionalDuration = 1 << difficulty;
                            int duration = freezeInstance.getDuration() + additionalDuration;
                            int lvl = duration / 120;
                            targetLiving.addEffect(new EffectInstance(ModEffects.FREEZE.get(), duration, lvl));
                        }
                    }
                }
            }
        }
    }
    
    public static boolean onUserAttacked(LivingAttackEvent event) {
        Entity attacker = event.getSource().getDirectEntity();
        if (attacker instanceof LivingEntity && !attacker.isOnFire() && !ModDamageSources.isImmuneToCold(attacker)) {
            LivingEntity targetLiving = event.getEntityLiving();
            return INonStandPower.getNonStandPowerOptional(targetLiving).map(power -> {
                if (power.getHeldAction(true) == ModActions.VAMPIRISM_FREEZE.get()) {
                    World world = attacker.level;
                    int difficulty = world.getDifficulty().getId();
                    ((LivingEntity) attacker).addEffect(new EffectInstance(ModEffects.FREEZE.get(), difficulty * 60, difficulty));
                    world.playSound(null, attacker, ModSounds.VAMPIRE_FREEZE.get(), attacker.getSoundSource(), 1.0F, 1.0F);
                    return true;
                }
                return false;
            }).orElse(false);
        }
        return false;
    }

    @Override
    public boolean isHeldSentToTracking() {
        return true;
    }
    
    @Override
    public void onHoldTickClientEffect(LivingEntity user, IPower<?> power, int ticksHeld, boolean requirementsFulfilled, boolean stateRefreshed) {
        if (requirementsFulfilled) {
            Vector3d particlePos = user.position().add(
                    (Math.random() - 0.5) * (user.getBbWidth() + 1.0), 
                    Math.random() * (user.getBbHeight() + 1.0), 
                    (Math.random() - 0.5) * (user.getBbWidth() + 1.0));
            user.level.addParticle(ParticleTypes.CLOUD, particlePos.x, particlePos.y, particlePos.z, 0, 0, 0);
        }
    }
}
