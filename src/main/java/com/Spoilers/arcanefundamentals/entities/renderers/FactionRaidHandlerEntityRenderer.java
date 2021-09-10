package com.Spoilers.arcanefundamentals.entities.renderers;

import com.Spoilers.arcanefundamentals.entities.FactionRaidHandlerEntity;
import com.Spoilers.arcanefundamentals.util.AFRenderTypes;
import com.Spoilers.arcanefundamentals.util.RenderUtil;
import com.ma.api.capabilities.Faction;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

public class FactionRaidHandlerEntityRenderer extends EntityRenderer<FactionRaidHandlerEntity> {
    private static final int[] white = new int[]{255, 255, 255};
    private static final int[] green = new int[]{0, 204, 0};
    private static final int[] blue = new int[]{102, 0, 255};

    public FactionRaidHandlerEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    public void render(FactionRaidHandlerEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if (entityIn.getFaction() == Faction.ANCIENT_WIZARDS) {
            this.RenderPortal(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        } else if (entityIn.getFaction() == Faction.FEY_COURT) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.0, 1.0, 0.0);
            RenderUtil.renderRadiant(entityIn, matrixStackIn, bufferIn, green, blue, 128, 0.5f);
            matrixStackIn.popPose();
        }
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    private void RenderPortal(FactionRaidHandlerEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        Quaternion cameraRotation = this.entityRenderDispatcher.cameraOrientation();
        Quaternion portalRotation = new Quaternion(0.0f, cameraRotation.j(), 0.0f, cameraRotation.r());
        float spawnPct = (float)Math.min(entityIn.tickCount, 20) / 20.0f;
        float scaleFactor = 4.0f * spawnPct;
        float portalSpinDegrees = entityIn.tickCount * 3 % 360;
        float verticalOffset = 1.0f;
        int[] colors = white;
        IVertexBuilder vertexBuilder = bufferIn.getBuffer(AFRenderTypes.RAID_PORTAL_HANDLER_RENDER);
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0, verticalOffset, 0.0);
        matrixStackIn.scale(scaleFactor, scaleFactor, scaleFactor);
        matrixStackIn.mulPose(portalRotation);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0f));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(portalSpinDegrees));
        matrixStackIn.translate(0.0, -0.25, 0.0);
        MatrixStack.Entry matrixStackEntry = matrixStackIn.last();
        Matrix4f renderMatrix = matrixStackEntry.pose();
        Matrix3f normalMatrix = matrixStackEntry.normal();
        float nrmV = (float)Math.cos(portalSpinDegrees * Math.PI / 180.0);
        float nrmH = (float)Math.cos((portalSpinDegrees - 90.0f) * Math.PI / 180.0);
        int frame = entityIn.tickCount % 25;
        float minU = 0.0f;
        float maxU = 1.0f;
        float minV = 0.04f * (float)frame;
        float maxV = minV + 0.04f;
        FactionRaidHandlerEntityRenderer.addVertex(vertexBuilder, renderMatrix, normalMatrix, packedLightIn, 0.0f, 0.0f, minU, maxV, nrmH, nrmV, colors);
        FactionRaidHandlerEntityRenderer.addVertex(vertexBuilder, renderMatrix, normalMatrix, packedLightIn, 1.0f, 0.0f, maxU, maxV, nrmH, nrmV, colors);
        FactionRaidHandlerEntityRenderer.addVertex(vertexBuilder, renderMatrix, normalMatrix, packedLightIn, 1.0f, 1.0f, maxU, minV, nrmH, nrmV, colors);
        FactionRaidHandlerEntityRenderer.addVertex(vertexBuilder, renderMatrix, normalMatrix, packedLightIn, 0.0f, 1.0f, minU, minV, nrmH, nrmV, colors);
        matrixStackIn.popPose();
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0, verticalOffset, 0.0);
        matrixStackIn.scale(scaleFactor, scaleFactor, scaleFactor);
        matrixStackIn.mulPose(portalRotation);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0f));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(-portalSpinDegrees));
        matrixStackIn.translate(0.0, -0.25, 0.001f);
        matrixStackEntry = matrixStackIn.last();
        renderMatrix = matrixStackEntry.pose();
        normalMatrix = matrixStackEntry.normal();
        nrmH = (float)Math.cos((portalSpinDegrees + 90.0f) * Math.PI / 180.0);
        FactionRaidHandlerEntityRenderer.addVertex(vertexBuilder, renderMatrix, normalMatrix, packedLightIn, 0.0f, 0.0f, minU, maxV, nrmH, nrmV, colors);
        FactionRaidHandlerEntityRenderer.addVertex(vertexBuilder, renderMatrix, normalMatrix, packedLightIn, 1.0f, 0.0f, maxU, maxV, nrmH, nrmV, colors);
        FactionRaidHandlerEntityRenderer.addVertex(vertexBuilder, renderMatrix, normalMatrix, packedLightIn, 1.0f, 1.0f, maxU, minV, nrmH, nrmV, colors);
        FactionRaidHandlerEntityRenderer.addVertex(vertexBuilder, renderMatrix, normalMatrix, packedLightIn, 0.0f, 1.0f, minU, minV, nrmH, nrmV, colors);
        matrixStackIn.popPose();
    }

    private static void addVertex(IVertexBuilder vertexBuilder_, Matrix4f renderMatrix, Matrix3f normalMatrix, int packedLight, float x, float y, float u, float v, float nrmH, float nrmV, int[] rgb) {
        vertexBuilder_.vertex(renderMatrix, x - 0.5f, y - 0.25f, 0.0f).color(rgb[0], rgb[1], rgb[2], 230).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normalMatrix, nrmH, nrmV, nrmH).endVertex();
    }

    public ResourceLocation getTextureLocation(FactionRaidHandlerEntity entity) {
        return AFRenderTypes.PORTAL_TEXTURE_RAID;
    }
}