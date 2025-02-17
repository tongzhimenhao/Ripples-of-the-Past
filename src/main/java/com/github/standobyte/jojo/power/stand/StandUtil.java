package com.github.standobyte.jojo.power.stand;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.github.standobyte.jojo.JojoModConfig;
import com.github.standobyte.jojo.capability.entity.power.StandCapProvider;
import com.github.standobyte.jojo.capability.world.SaveFileUtilCapProvider;
import com.github.standobyte.jojo.client.StandControlHandler;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.init.ModStandTypes;
import com.github.standobyte.jojo.network.PacketManager;
import com.github.standobyte.jojo.network.packets.fromserver.SyncStandControlStatusPacket;
import com.github.standobyte.jojo.power.stand.type.StandType;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class StandUtil {
    public static final int MAX_TIER = 6;
    public static final int[] TIER_XP_LEVELS = {0, 1, 10, 20, 30, 40, 55};
    
    public static StandType randomStandByTier(int tier, LivingEntity entity, Random random) {
        if (!entity.level.isClientSide()) {
            Collection<StandType> stands = ModStandTypes.Registry.getRegistry().getValues();
            List<StandType> filtered = 
                    stands.stream()
                    .filter(stand -> (tier < 0 || stand.getTier() == tier) && !JojoModConfig.COMMON.isStandBanned(stand))
                    .collect(Collectors.toList());
            
            if (filtered.isEmpty()) {
                if (tier > -1 && StandUtil.arrowPoolNextTier(tier) > -1) {
                    entity.sendMessage(new TranslationTextComponent("jojo.chat.message.tmp_not_enough_xp"), Util.NIL_UUID); // TODO remove the message after adding stands for each tier
                }
                return null;
            }
            
            if (JojoModConfig.COMMON.prioritizeLeastTakenStands.get()) {
                filtered = SaveFileUtilCapProvider.getSaveFileCap((ServerWorld) entity.level).leastTakenStands(filtered);
            }
            
            if (!filtered.isEmpty()) {
                return filtered.get(random.nextInt(filtered.size()));
            }
        }
        return null;
    }
    
    public static int standTierFromXp(int xpLvl, boolean withConfigBans) {
        for (int i = MAX_TIER; i >= 0; i--) {
            if (xpLvl >= tierLowerBorder(i) && (!withConfigBans || JojoModConfig.COMMON.tierHasUnbannedStands(i))) {
                return i;
            }
        }
        return -1;
    }
    
    public static int arrowPoolNextTier(int startingTier) {
        for (int i = startingTier + 1; i <= MAX_TIER; i++) {
            if (JojoModConfig.COMMON.tierHasUnbannedStands(i)) {
                return i;
            }
        }
        return -1;
    }
    
    public static int tierLowerBorder(int tier) {
        return TIER_XP_LEVELS[tier];
    }
    
    public static boolean isPlayerStandUser(PlayerEntity player) {
        return player.getCapability(StandCapProvider.STAND_CAP).map(cap -> cap.hasPower()).orElse(false);
    }
    
    public static void setManualControl(PlayerEntity player, boolean manualControl, boolean keepPosition) {
        IStandPower standPower = IStandPower.getPlayerStandPower(player);
        IStandManifestation stand = standPower.getStandManifestation();
        if (stand instanceof StandEntity) {
            StandEntity standEntity = ((StandEntity) stand);
            if (!standEntity.isArmsOnlyMode()) {
                if (!player.level.isClientSide()) {
                    standEntity.setManualControl(manualControl, keepPosition);
                    PacketManager.sendToClient(new SyncStandControlStatusPacket(manualControl, keepPosition), (ServerPlayerEntity) player);
                }
                else {
                    Minecraft.getInstance().setCameraEntity(manualControl ? standEntity : player);
                    if (manualControl) {
                        StandControlHandler.setStartedControllingStand();
                    }
                }
            }
        }
    }
}
