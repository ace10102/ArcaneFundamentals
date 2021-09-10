package com.Spoilers.arcanefundamentals.util;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

public class RenderUtil {
	
	private static final float TRIANGLE_CONSTANT = (float)(Math.sqrt(3.0) / 2.0);
	
	 public static void renderRadiant(float ageTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int[] innerColor, int[] outerColor, int alpha, float scale, boolean grow) {
	        float rotationByAge = ageTicks / 180.0f;
	        Random random = new Random(1234L);
	        IVertexBuilder lightingBuilder = bufferIn.getBuffer(AFRenderTypes.RADIANT_RENDER_TYPE);
	        matrixStackIn.pushPose();
	        if (grow) {
	            float growth = 1.0f + scale * 25.0f;
	            matrixStackIn.scale(growth, growth, growth);
	        } else {
	            matrixStackIn.scale(scale, scale, scale);
	        }
	        alpha = Math.min(alpha, 64);
	        for (int i = 0; i < 40; ++i) {
	            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(random.nextFloat() * 360.0f));
	            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(random.nextFloat() * 360.0f));
	            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(random.nextFloat() * 360.0f));
	            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(random.nextFloat() * 360.0f));
	            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(random.nextFloat() * 360.0f));
	            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(random.nextFloat() * 360.0f + 90.0f * rotationByAge));
	            float hOffset = i % 3 == 0 ? random.nextFloat() * 0.55f : random.nextFloat() * 0.25f;
	            float vOffset = random.nextFloat() * 0.25f;
	            Matrix4f currentMatrix = matrixStackIn.last().pose();
	            startRadiantQuad(lightingBuilder, currentMatrix, innerColor, alpha);
	            addRadiantPoint_NegativeOffset(lightingBuilder, currentMatrix, hOffset, vOffset, outerColor);
	            addRadiantPoint_PositiveOffset(lightingBuilder, currentMatrix, hOffset, vOffset, innerColor);
	            startRadiantQuad(lightingBuilder, currentMatrix, innerColor, alpha);
	            addRadiantPoint_PositiveOffset(lightingBuilder, currentMatrix, hOffset, vOffset, outerColor);
	            addRadiantPoint_AbsolutePosition(lightingBuilder, currentMatrix, hOffset, vOffset, innerColor);
	            startRadiantQuad(lightingBuilder, currentMatrix, innerColor, alpha);
	            addRadiantPoint_AbsolutePosition(lightingBuilder, currentMatrix, hOffset, vOffset, outerColor);
	            addRadiantPoint_NegativeOffset(lightingBuilder, currentMatrix, hOffset, vOffset, innerColor);
	        }
	        matrixStackIn.popPose();
	    }

	    public static void renderRadiant(Entity entityIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int[] innerColor, int[] outerColor, int alpha, float scale) {
	        renderRadiant(entityIn.tickCount, matrixStackIn, bufferIn, innerColor, outerColor, alpha, scale, true);
	    }
	    
	    private static void startRadiantQuad(IVertexBuilder vertexBuilder, Matrix4f renderMatrix, int[] rgb, int alpha) {
	        vertexBuilder.vertex(renderMatrix, 0.0f, 0.0f, 0.0f).color(255, 255, 255, alpha).endVertex();
	        vertexBuilder.vertex(renderMatrix, 0.0f, 0.0f, 0.0f).color(rgb[0], rgb[1], rgb[2], alpha).endVertex();
	    }

	    private static void addRadiantPoint_NegativeOffset(IVertexBuilder vertexBuilder, Matrix4f renderMatrix, float y, float x, int[] rgb) {
	        vertexBuilder.vertex(renderMatrix, -TRIANGLE_CONSTANT * x, y, -0.2f * x).color(rgb[0], rgb[1], rgb[2], 0).endVertex();
	    }

	    private static void addRadiantPoint_PositiveOffset(IVertexBuilder vertexBuilder, Matrix4f renderMatrix, float y, float x, int[] rgb) {
	        vertexBuilder.vertex(renderMatrix, TRIANGLE_CONSTANT * x, y, -0.2f * x).color(rgb[0], rgb[1], rgb[2], 0).endVertex();
	    }

	    private static void addRadiantPoint_AbsolutePosition(IVertexBuilder vertexBuilder, Matrix4f renderMatrix, float y, float z, int[] rgb) {
	        vertexBuilder.vertex(renderMatrix, 0.0f, y, 1.0f * z).color(rgb[0], rgb[1], rgb[2], 0).endVertex();
	    }
}
