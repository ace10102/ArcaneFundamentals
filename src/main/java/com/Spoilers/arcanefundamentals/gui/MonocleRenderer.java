package com.Spoilers.arcanefundamentals.gui;

import java.util.Optional;
import java.util.Stack;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import com.Spoilers.arcanefundamentals.items.AFItems;
import com.Spoilers.arcanefundamentals.util.AFRenderType;
import com.google.common.collect.ImmutableList;
import com.ma.api.ManaAndArtificeMod;
import com.ma.api.capabilities.IPlayerMagic;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;

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
		
		/*Entity focused = getEntityLookedAt(mc.player);
		if (focused != null && focused instanceof LivingEntity && focused.isAlive()) {
			renderHealthBar((LivingEntity) focused, mc, matrixStack, partialTicks, renderInfo, cameraEntity);
		}*/
		
		Vector3d cameraPos = renderInfo.getPosition();
		final ClippingHelper clippingHelper = new ClippingHelper(matrixStack.last().pose(), event.getProjectionMatrix());
		clippingHelper.prepare(cameraPos.x(), cameraPos.y(), cameraPos.z());

		ClientWorld client = mc.level;
		if (client != null) {
			for (Entity entity : client.entitiesForRendering()) {
				if (entity != null && entity instanceof LivingEntity && entity != cameraEntity && entity.isAlive() && entity.getIndirectPassengers().isEmpty() && entity.shouldRender(cameraPos.x(), cameraPos.y(), cameraPos.z()) && (entity.noCulling || clippingHelper.isVisible(entity.getBoundingBox()))) {
					renderHealthBar((LivingEntity) entity, mc, matrixStack, partialTicks, renderInfo, cameraEntity);
					//System.out.println(entity.getEntityString());
				}
			}
		}
	}
	
	public void renderHealthBar(LivingEntity renderedEntity, Minecraft mc, MatrixStack matrixStack, float partialTicks, ActiveRenderInfo renderInfo, Entity viewPoint) {
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
			//boolean boss = !entity.isNonBoss();

			String entityID = entity.getType().getRegistryName().toString();
			//if (NeatConfig.blacklist.contains(entityID))
				//continue;

			processing:
			{
				float distance = renderedEntity.distanceTo(viewPoint);
				if (distance > 16 || !renderedEntity.canSee(viewPoint) || entity.isInvisible())
					break processing;
				//if (!NeatConfig.showOnBosses && !boss)
					//break processing;
				//if (!NeatConfig.showOnPlayers && entity instanceof PlayerEntity)
					//break processing;
				if (entity.getMaxHealth() <= 0)
					break processing;
				//if (!NeatConfig.showFullHealth && entity.getHealth() == entity.getMaxHealth())
					//break processing;

				double x = renderedEntity.xo + (renderedEntity.getX() - renderedEntity.xo) * partialTicks;
				double y = renderedEntity.yo + (renderedEntity.getY() - renderedEntity.yo) * partialTicks;
				double z = renderedEntity.zo + (renderedEntity.getZ() - renderedEntity.zo) * partialTicks;

				EntityRendererManager renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
				Vector3d renderPos = renderManager.camera.getPosition();

				matrixStack.pushPose();
				matrixStack.translate((float) (x - renderPos.x()), (float) (y - renderPos.y() + renderedEntity.getBbHeight() /*+ NeatConfig.heightAbove*/), (float) (z - renderPos.z()));
				IRenderTypeBuffer.Impl buffer = mc.renderBuffers().bufferSource();
				//ItemStack icon = getIcon(entity, boss);
				final int light = 0xF000F0;
				//renderEntity(mc, matrixStack, buffer, renderInfo, entity, light/*, icon, boss*/);
				renderTest(mc, matrixStack, buffer, renderInfo, entity, light/*, icon, boss*/);
				matrixStack.popPose();

				//matrixStack.translate(0.0D, -(NeatConfig.backgroundHeight + NeatConfig.barHeight + NeatConfig.backgroundPadding), 0.0D);
			}
		}
		matrixStack.popPose();
	}
	private void renderTest(Minecraft mc, MatrixStack matrixStack, IRenderTypeBuffer.Impl buffer, ActiveRenderInfo renderInfo, LivingEntity entity, int light/*, ItemStack icon, boolean boss*/) {
		Quaternion rotation = renderInfo.rotation().copy();
		rotation.mul(-1.0F);
		matrixStack.mulPose(rotation);
		float scale = 0.026666672F;
		matrixStack.scale(-scale, -scale, scale);
		float health = MathHelper.clamp(entity.getHealth(), 0.0F, entity.getMaxHealth());
		float percent = (health / entity.getMaxHealth()) * 100.0F;
		float size = 10;//NeatConfig.plateSize;
		float textScale = 0.5F;

		String name = (entity.hasCustomName() ? entity.getCustomName() : entity.getDisplayName()).getString();
		if (entity.hasCustomName())
			name = TextFormatting.ITALIC + name;

		float namel = mc.font.width(name) * textScale;
		if (namel + 20 > size * 2) {
			size = namel / 2.0F + 10.0F;
		}
		float healthSize = size * (health / entity.getMaxHealth());
		MatrixStack.Entry entry = matrixStack.last();
		Matrix4f modelViewMatrix = entry.pose();
		Vector3f normal = new Vector3f(0.0F, 1.0F, 0.0F);
		normal.transform(entry.normal());
		
		IVertexBuilder builder = buffer.getBuffer(AFRenderType.getManaBarType(AFRenderType.MANA_BAR_TEXTURE));
		float padding = 10;//NeatConfig.backgroundPadding;
		int bgHeight = 10;//NeatConfig.backgroundHeight;
		int barHeight = 10;//NeatConfig.barHeight;

		// Background
		if (1>0) {
			builder.vertex(modelViewMatrix, -size - padding, -bgHeight, 0.01F).uv(0.0F, 0.0F).color(0, 0, 0, 64).normal(normal.x(), normal.y(), normal.z()).uv2(light).endVertex();
			builder.vertex(modelViewMatrix, -size - padding, barHeight + padding, 0.01F).uv(0.0F, 0.5F).color(0, 0, 0, 64).normal(normal.x(), normal.y(), normal.z()).uv2(light).endVertex();
			builder.vertex(modelViewMatrix, size + padding, barHeight + padding, 0.01F).uv(1.0F, 0.5F).color(0, 0, 0, 64).normal(normal.x(), normal.y(), normal.z()).uv2(light).endVertex();
			builder.vertex(modelViewMatrix, size + padding, -bgHeight, 0.01F).uv(1.0F, 0.0F).color(0, 0, 0, 64).normal(normal.x(), normal.y(), normal.z()).uv2(light).endVertex();
		}
	}
}
