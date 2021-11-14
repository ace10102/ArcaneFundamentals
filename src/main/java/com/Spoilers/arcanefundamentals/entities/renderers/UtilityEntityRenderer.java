package com.Spoilers.arcanefundamentals.entities.renderers;

import java.util.Random;

import com.Spoilers.arcanefundamentals.entities.RitualUtilityEntity;
//import com.Spoilers.arcanefundamentals.util.RenderUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.registries.ForgeRegistries;

public class UtilityEntityRenderer extends EntityRenderer<RitualUtilityEntity> {

    public UtilityEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    public void render(RitualUtilityEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        switch (entityIn.getRitual()) {

        case "arcanefundamentals:ritual-effect-catharsis": {
            if (entityIn.getState() == 0) {
                World world = entityIn.level;
                BlockPos pos = entityIn.blockPosition();
                BlockState state = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("mana-and-artifice:chalk_rune")).defaultBlockState();
                float scale = 0.75f;
                matrixStackIn.pushPose();
                matrixStackIn.scale(scale, scale, scale);
                matrixStackIn.translate((0 / scale), 0.05f, (0 / scale));
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(entityIn.tickCount + partialTicks));
                IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderTypeLookup.getRenderType(state, false));
                Minecraft mc = Minecraft.getInstance();
                IBakedModel model = mc.getModelManager().getModel(new ResourceLocation("mana-and-artifice", "block/arcane_sentry"));
                IModelData data = model.getModelData(world, pos, state, ModelDataManager.getModelData(world, pos));
                Random random = new Random(1234L);
                for (BakedQuad quad : model.getQuads(null, null, random, data)) {
                    vertexBuilder.addVertexData(matrixStackIn.last(), quad, 1.0f, 0.25f, 1.0f, 1.0f, -1, 655360, true);
                }
                matrixStackIn.translate((0 / scale), 1.3d, (0 / scale));
                for (BakedQuad quad : model.getQuads(null, null, random, data)) {
                    vertexBuilder.addVertexData(matrixStackIn.last(), quad, 0.4f, 0.25f, 0.9f, 0.9f, 1, 655360, true);
                }
                matrixStackIn.popPose();
            }
        }
        }
    }

    public ResourceLocation getTextureLocation(RitualUtilityEntity entity) {
        return null;
    }
}