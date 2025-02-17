package com.github.standobyte.jojo.action.actions;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.ActionTarget.TargetType;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntity.StandPose;
import com.github.standobyte.jojo.init.ModSounds;
import com.github.standobyte.jojo.power.IPower;
import com.github.standobyte.jojo.power.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.stand.IStandPower;
import com.github.standobyte.jojo.util.JojoModUtil;
import com.github.standobyte.jojo.util.TimeHandler;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class TimeStopInstant extends StandEntityAction {

    public TimeStopInstant(Builder builder) {
        super(builder);
        this.doNotAutoSummonStand = true;
    }
    
    @Override
    public ActionConditionResult checkConditions(LivingEntity user, LivingEntity performer, IPower<?> power, ActionTarget target) {
        if (TimeHandler.isTimeStopped(user.level, user.blockPosition())) {
            return ActionConditionResult.NEGATIVE;
        }
        return ActionConditionResult.POSITIVE;
    }
    
    @Override
    public void perform(World world, LivingEntity user, IPower<?> power, ActionTarget target) {
        IStandPower standPower = (IStandPower) power;
        StandEntity stand = power.isActive() ? (StandEntity) getPerformer(user, power) : null;
        int timeStopTicks = TimeHandler.getTimeStopTicks(getExpRequirement(), standPower, user, INonStandPower.getNonStandPowerOptional(user));
        JojoModUtil.playSound(world, user instanceof PlayerEntity ? (PlayerEntity) user : null, user.getX(), user.getY(), user.getZ(), 
                ModSounds.TIME_STOP_BLINK.get(), SoundCategory.AMBIENT, 5.0F, 1.0F, TimeHandler::canPlayerSeeInStoppedTime);
        if (!world.isClientSide()) {
            Vector3d blinkPos = null;
            if (target.getType() == TargetType.EMPTY) {
                double speed = user.getSpeed() * 2.1585;
                RayTraceResult rayTrace = JojoModUtil.rayTrace(user, speed * timeStopTicks, null);
                if (rayTrace.getType() == RayTraceResult.Type.MISS) {
                    blinkPos = rayTrace.getLocation();
                }
                target = ActionTarget.fromRayTraceResult(rayTrace);
            }
            switch (target.getType()) {
            case ENTITY:
                blinkPos = target.getTargetPos();
                break;
            case BLOCK:
                BlockPos blockPosTargeted = target.getBlockPos();
                blinkPos = Vector3d.atBottomCenterOf(world.isEmptyBlock(blockPosTargeted.above()) ? blockPosTargeted.above() : blockPosTargeted.relative(target.getFace()));
                break;
            default:
                Vector3d pos = blinkPos;
                BlockPos blockPos = new BlockPos(pos);
                while (world.isEmptyBlock(blockPos.below()) && blockPos.getY() > 0) {
                    blockPos = blockPos.below();
                }
                blinkPos = new Vector3d(pos.x, blockPos.getY() > 0 ? blockPos.getY() : user.position().y, pos.z);
                break;
            }
            
            user.teleportTo(blinkPos.x, blinkPos.y, blinkPos.z);
            standPower.setExp(standPower.getExp() + 4);
            if (stand != null) {
                stand.setStandPose(StandPose.NONE);
                stand.setAlpha(1.0F);
            }
        }
    }
}
