package com.github.standobyte.jojo.action.actions;

import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.ActionTarget.TargetType;
import com.github.standobyte.jojo.entity.damaging.projectile.MRCrossfireHurricaneEntity;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntity.StandPose;
import com.github.standobyte.jojo.init.ModSounds;
import com.github.standobyte.jojo.power.IPower;
import com.github.standobyte.jojo.util.MathUtil;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.world.World;

public class MagiciansRedCrossfireHurricane extends StandEntityAction {

    public MagiciansRedCrossfireHurricane(StandEntityAction.Builder builder) {
        super(builder);
    }
    
    @Override
    public void onStartedHolding(World world, LivingEntity user, IPower<?> power, ActionTarget target, boolean requirementsFulfilled) {
        if (!world.isClientSide() && requirementsFulfilled) {
            LivingEntity entity = getPerformer(user, power);
            if (entity instanceof StandEntity) {
                ((StandEntity) entity).setStandPose(StandPose.RANGED_ATTACK);
            }
        }
    }

    @Override
    public void perform(World world, LivingEntity user, IPower<?> power, ActionTarget target) {
        if (!world.isClientSide()) {
            LivingEntity stand = getPerformer(user, power);
            boolean special = isShiftVariation();
            int n = special ? 8 : 1;
            double distance = 32;
            if (special) {
                if (target.getType() == TargetType.EMPTY) {
                    target = this.aim(world, user, power, distance);
                }
            }
            for (int i = 0; i < n; i++) {
                MRCrossfireHurricaneEntity ankh = new MRCrossfireHurricaneEntity(special, stand, world);
                Vector2f rotOffsets = i == 0 ? Vector2f.ZERO
                        : MathUtil.xRotYRotOffsets(((double) i / (double) n + 0.5) * Math.PI, 2);
                if (special && target.getType() != TargetType.EMPTY) {
                    ankh.setSpecial(target.getTargetPos());
                }
                ankh.shootFromRotation(stand, stand.xRot + rotOffsets.x, stand.yRot + rotOffsets.y, 0, special ? 1.0F : 0.75F, 0.0F);
                world.addFreshEntity(ankh);
            }
            stand.playSound(ModSounds.MAGICIANS_RED_CROSSFIRE_HURRICANE.get(), 1.0F, 1.0F);
        }
    }
    
    @Override
    public void onStoppedHolding(World world, LivingEntity user, IPower<?> power, int ticksHeld) {
        if (!world.isClientSide()) {
            LivingEntity entity = getPerformer(user, power);
            if (entity instanceof StandEntity) {
                ((StandEntity) entity).setStandPose(StandPose.NONE);
            }
        }
    }
}
