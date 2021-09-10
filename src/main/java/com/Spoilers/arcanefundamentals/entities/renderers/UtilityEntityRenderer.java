package com.Spoilers.arcanefundamentals.entities.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class UtilityEntityRenderer extends EntityRenderer<Entity> {

    public UtilityEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    public void render(Entity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
    	
    }

    public ResourceLocation getTextureLocation(Entity entity) {
        return null;
    }
}