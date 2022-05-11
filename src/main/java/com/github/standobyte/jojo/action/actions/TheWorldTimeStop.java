package com.github.standobyte.jojo.action.actions;

import com.github.standobyte.jojo.power.stand.IStandPower;

public class TheWorldTimeStop extends TimeStop {

    public TheWorldTimeStop(Builder builder) {
        super(builder);
    }

    @Override
    protected boolean autoSummonStand(IStandPower power) {
        return super.autoSummonStand(power) || power.getResolveLevel() < 3;
    }
    
    @Override
    public int getHoldDurationToFire(IStandPower power) { 
        return shortedHoldDuration(power, super.getHoldDurationToFire(power));
    }
    
    private int shortedHoldDuration(IStandPower power, int ticks) {
        return ticks > 0 && power.getResolveLevel() >= 4 ? 20 : ticks;
    }
    
    @Override
    public boolean cancelHeldOnGettingAttacked(IStandPower power) {
        return true;
    }
}
