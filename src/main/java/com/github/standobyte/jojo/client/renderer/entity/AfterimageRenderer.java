package com.github.standobyte.jojo.client.renderer.entity;

import com.github.standobyte.jojo.entity.AfterimageEntity;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

public class AfterimageRenderer<T extends AfterimageEntity> extends EntityRenderer<T> {

    public AfterimageRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public ResourceLocation getTextureLocation(T p_110775_1_) {
        return null;
    }

    @Override
    public void render(T entity, float yRotation, float partialTick, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        Entity originEntity = entity.getOriginEntity();
        if (originEntity != null) {
            Minecraft mc = Minecraft.getInstance();
            if (originEntity == mc.getCameraEntity() && mc.options.getCameraType().isFirstPerson()) {
                Vector3d diff = entity.getPosition(partialTick).subtract(originEntity.getPosition(partialTick));
                if (diff.lengthSqr() < 0.25D) {
                    return;
                }
            }
            entityRenderDispatcher.getRenderer(originEntity).render(originEntity, yRotation, partialTick, matrixStack, buffer, packedLight);
        }
        super.render(entity, yRotation, partialTick, matrixStack, buffer, packedLight);
    }

}
