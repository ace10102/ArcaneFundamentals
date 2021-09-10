/*package com.Spoilers.arcanefundamentals.gui;

import java.util.Optional;
import java.util.Stack;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import com.Spoilers.arcanefundamentals.config.AFClientConfig;
import com.Spoilers.arcanefundamentals.items.AFItems;
import com.Spoilers.arcanefundamentals.util.AFRenderType;
import com.ma.api.ManaAndArtificeMod;
import com.ma.api.capabilities.IPlayerMagic;

import top.theillusivec4.curios.api.CuriosApi;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MonocleRenderer {
	
	private final Minecraft mc = Minecraft.getInstance();
	
	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		
		if (!Minecraft.renderNames())
			return;
		
		ClientPlayerEntity player = mc.player;
		Optional<ImmutableTriple<String, Integer, ItemStack>> equipped = CuriosApi.getCuriosHelper().findEquippedCurio(AFItems.MANA_MONOCLE.get(), player);
		if (!equipped.isPresent() || !((ImmutableTriple<String, Integer, ItemStack>)equipped.get()).getRight().getItem().equals(AFItems.MANA_MONOCLE.get())) {
			return;
		}
		IPlayerMagic magic = (IPlayerMagic)player.getCapability(ManaAndArtificeMod.getMagicCapability()).orElse(null);
		if (magic == null || !magic.isMagicUnlocked()) {
			return;
		}
		
		ActiveRenderInfo renderInfo = mc.gameRenderer.getMainCamera();
		MatrixStack matrixStack = event.getMatrixStack();
		float partialTicks = event.getPartialTicks();
		Entity cameraEntity = renderInfo.getEntity() != null ? renderInfo.getEntity() : mc.player;
		
		Entity focused = getEntityLookedAt(mc.player);
		if (focused != null && focused instanceof LivingEntity && focused.isAlive()) {
			renderManaBar((LivingEntity) focused, mc, matrixStack, partialTicks, renderInfo, cameraEntity);
		}
		
		Vector3d cameraPos = renderInfo.getPosition();
		final ClippingHelper clippingHelper = new ClippingHelper(matrixStack.last().pose(), event.getProjectionMatrix());
		clippingHelper.prepare(cameraPos.x(), cameraPos.y(), cameraPos.z());

		ClientWorld client = mc.level;
		if (client != null) {
			for (Entity entity : client.entitiesForRendering()) {
				if (entity != null && entity instanceof LivingEntity && entity != cameraEntity && entity.isAlive() && entity.getIndirectPassengers().isEmpty() && entity.shouldRender(cameraPos.x(), cameraPos.y(), cameraPos.z()) && (entity.noCulling || clippingHelper.isVisible(entity.getBoundingBox()))) {
					renderManaBar((LivingEntity) entity, mc, matrixStack, partialTicks, renderInfo, cameraEntity);
					//System.out.println(entity);
				}
			}
		}
	}
	
	public void renderManaBar(LivingEntity renderedEntity, Minecraft mc, MatrixStack matrixStack, float partialTicks, ActiveRenderInfo renderInfo, Entity viewPoint) {
		Stack<LivingEntity> ridingStack = new Stack<>();

		LivingEntity entity = renderedEntity;
		ridingStack.push(entity);

		while (entity.getVehicle() != null && entity.getVehicle() instanceof LivingEntity) {
			entity = (LivingEntity) entity.getVehicle();
			ridingStack.push(entity);
		}

		matrixStack.pushPose();
		while (!ridingStack.isEmpty()) {
			entity = ridingStack.pop();
			
			ResourceLocation souls = new ResourceLocation("mana-and-artifice", "souls");
			EntityType<?> entityID = entity.getType();//.getRegistryName().toString();
			//System.out.println(((PlayerEntity)entity).getCapability(ManaAndArtificeMod.getMagicCapability()).orElse(null));
			processing:
			{
				float distance = renderedEntity.distanceTo(viewPoint);
				if (distance > AFClientConfig.ELEMENT_RENDER_DISTANCE.get() || !renderedEntity.canSee(viewPoint) || entity.isInvisible())
					break processing;
//				if (!(entity instanceof PlayerEntity)) {
//					break processing;}
//				IPlayerMagic targetMagic = ((PlayerEntity)entity).getCapability(ManaAndArtificeMod.getMagicCapability()).orElse(null);
//				if (targetMagic == null /* || !targetMagic.isMagicUnlocked() *//*) {
//					System.out.println(targetMagic.getCastingResource().getAmount());
//					break processing;}
//				if (!AFClientConfig.DISPLAY_SOULS.get() && targetMagic.getCastingResource().getRegistryName().equals(souls))
//					break processing;
				
				double x = renderedEntity.xo + (renderedEntity.getX() - renderedEntity.xo) * partialTicks;
				double y = renderedEntity.yo + (renderedEntity.getY() - renderedEntity.yo) * partialTicks;
				double z = renderedEntity.zo + (renderedEntity.getZ() - renderedEntity.zo) * partialTicks;

				EntityRendererManager renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
				Vector3d renderPos = renderManager.camera.getPosition();

				matrixStack.pushPose();
				matrixStack.translate((float)(x - renderPos.x()), (float)(y - renderPos.y() + renderedEntity.getBbHeight()), (float)(z - renderPos.z()));
				IRenderTypeBuffer.Impl buffer = mc.renderBuffers().bufferSource();
				final int light = 0xF000F0;
				renderElement(mc, matrixStack, buffer, renderInfo, entity, light);
				matrixStack.popPose();

				}
		}
		matrixStack.popPose();
	}
	private void renderElement(Minecraft mc, MatrixStack matrixStack, IRenderTypeBuffer.Impl buffer, ActiveRenderInfo renderInfo, LivingEntity entity, int light) {
		//rotate to follow camera
		Quaternion rotation = renderInfo.rotation().copy();
		//rotation.mul(-1F); //no idea what the point of this is but neat had it so it might do something
		matrixStack.mulPose(rotation);
		
		//setup for texture panel
		final float scale = 1f;
		float size = AFClientConfig.ELEMENT_SIZE.get().floatValue();//config
		float offsetY = AFClientConfig.ELEMENT_HEIGHT.get().floatValue()*4;//config
		float offsetX = -AFClientConfig.ELEMENT_OFFSET.get().floatValue();//config
		
		//texture panel matrix prep i guess
		matrixStack.scale(-scale, -scale/4, scale);
		MatrixStack.Entry entry = matrixStack.last();
		Matrix4f modelViewMatrix = entry.pose();
		Vector3f normal = new Vector3f(0.0F, 1.0F, 0.0F);
		normal.transform(entry.normal());
		
		IVertexBuilder builder = buffer.getBuffer(AFRenderType.getManaBarType(AFRenderType.MANA_BAR_TEXTURE));
		
		//Background texture panel
		builder.vertex(modelViewMatrix, -size-offsetX, -size-offsetY, 0.01F).uv(0F, 0F).normal(normal.x(), normal.y(), normal.z()).uv2(light).endVertex();
		builder.vertex(modelViewMatrix, -size-offsetX, size-offsetY, 0.01F).uv(0F, 1F).normal(normal.x(), normal.y(), normal.z()).uv2(light).endVertex();
		builder.vertex(modelViewMatrix, size-offsetX, size-offsetY, 0.01F).uv(1F, 1F).normal(normal.x(), normal.y(), normal.z()).uv2(light).endVertex();
		builder.vertex(modelViewMatrix, size-offsetX, -size-offsetY, 0.01F).uv(1F, 0F).normal(normal.x(), normal.y(), normal.z()).uv2(light).endVertex();
		
		//setup for proportional text translation
		//IPlayerMagic targetMagic = ((PlayerEntity)entity).getCapability(ManaAndArtificeMod.getMagicCapability()).orElse(null);
		//float manaf = ((targetMagic.getCastingResource().getAmount()));//grab entity mana somehow
		String mana = "000/000";//""+((double)manaf);
		float textLength = mc.font.width(mana);
		float factor = (textLength/2 < 18 ? 21 : 2 * textLength/3)/size;
		float textScale = (1/(factor));
		float textTall = mc.font.lineHeight;
		int white = 0xFFFFFF;
		int black = 0x000000;
			
		//text matrix setup
		matrixStack.scale(textScale, textScale*4, textScale);
		modelViewMatrix = matrixStack.last().pose();
		matrixStack.pushPose();
		
		//text positioning
		float textY = (float)(0.3-(offsetY*(factor/4))-(textTall/2.5F));//YHeight;
		float textX = (float)(0.575-(offsetX*factor)-(textLength/2));//XOffset
		
		//text render
		mc.font.drawInBatch(mana, textX, textY, white, false, modelViewMatrix, buffer, false, black, light);
			
		matrixStack.popPose();
	}
}*/