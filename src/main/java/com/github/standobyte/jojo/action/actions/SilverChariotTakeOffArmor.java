package com.github.standobyte.jojo.action.actions;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.entity.AfterimageEntity;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.stands.SilverChariotEntity;
import com.github.standobyte.jojo.init.ModSounds;
import com.github.standobyte.jojo.power.stand.IStandPower;

import net.minecraft.world.World;

public class SilverChariotTakeOffArmor extends StandEntityAction {

    public SilverChariotTakeOffArmor(StandEntityAction.Builder builder) {
        super(builder);
    }
    
    @Override
    protected ActionConditionResult checkStandConditions(StandEntity stand, IStandPower power, ActionTarget target) {
        if (stand instanceof SilverChariotEntity) {
            SilverChariotEntity chariot = (SilverChariotEntity) stand;
            if (chariot.isArmsOnlyMode()) {
                return ActionConditionResult.NEGATIVE;
            }
            if (!chariot.hasArmor()) {
                return conditionMessage("chariot_armor");
            }
            return ActionConditionResult.POSITIVE;
        }
        return ActionConditionResult.NEGATIVE;
    }
    
    @Override
    public void standTickPerform(World world, StandEntity standEntity, int ticks, IStandPower userPower, ActionTarget target) {
        if (!world.isClientSide() && ticks == 0) {
            if (standEntity instanceof SilverChariotEntity) {
                SilverChariotEntity chariot = (SilverChariotEntity) standEntity;
                chariot.setArmor(!chariot.hasArmor());
                for (int i = 1; i <= 10; i++) {
                    AfterimageEntity afterimage = new AfterimageEntity(world, chariot, i);
                    afterimage.setLifeSpan(Integer.MAX_VALUE);
                    world.addFreshEntity(afterimage);
                }
                chariot.playSound(ModSounds.SILVER_CHARIOT_ARMOR_OFF.get(), 1.0F, 1.0F);
            }
        }
    }
}
