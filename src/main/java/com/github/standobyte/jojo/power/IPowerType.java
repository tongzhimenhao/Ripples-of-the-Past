package com.github.standobyte.jojo.power;

import com.github.standobyte.jojo.action.Action;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IPowerType<P extends IPower<P, T>, T extends IPowerType<P, T>> extends IForgeRegistryEntry<T> {
    static final String NO_POWER_NAME = "";
    int getColor();
    boolean isReplaceableWith(T newType);
    boolean keepOnDeath(P power);
    void tickUser(LivingEntity entity, P power);
    default RayTraceResult clientHitResult(P power, Entity cameraEntity, RayTraceResult vanillaHitResult) {
        return vanillaHitResult;
    }
    Action<P>[] getAttacks();
    Action<P>[] getAbilities();
    int getExpRewardMultiplier();
    String getTranslationKey();
    ResourceLocation getIconTexture();
    String getEnergyString();
}
