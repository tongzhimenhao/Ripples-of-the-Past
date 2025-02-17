package com.github.standobyte.jojo.action.actions;

import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.HGGrapplingStringEntity;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntity.StandPose;
import com.github.standobyte.jojo.power.IPower;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class HierophantGreenGrapple extends StandEntityAction {
    public HierophantGreenGrapple(Builder builder) {
        super(builder);
    }
    
    @Override
    public void onStartedHolding(World world, LivingEntity user, IPower<?> power, ActionTarget target, boolean requirementsFulfilled) {
        if (!world.isClientSide() && requirementsFulfilled) {
            LivingEntity entity = getPerformer(user, power);
            if (entity instanceof StandEntity) {
                StandEntity stand = (StandEntity) entity;
                HGGrapplingStringEntity string = new HGGrapplingStringEntity(world, stand, power);
                if (isShiftVariation()) {
                    string.setBindEntities(true);
                }
                world.addFreshEntity(string);
                stand.setStandPose(StandPose.ABILITY);
            }
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
