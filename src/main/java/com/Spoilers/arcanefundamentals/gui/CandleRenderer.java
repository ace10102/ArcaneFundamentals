/*
package com.Spoilers.arcanefundamentals.gui;

import java.util.Optional;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import com.Spoilers.arcanefundamentals.items.AFItems;
import com.ma.api.ManaAndArtificeMod;
import com.ma.api.capabilities.IPlayerMagic;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;

public class CandleRenderer {
	
	private final Minecraft mc = Minecraft.getInstance();
	
	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		
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
		Vector3d cameraPos = renderInfo.getPosition();
		final ClippingHelper clippingHelper = new ClippingHelper(matrixStack.last().pose(), event.getProjectionMatrix());
		clippingHelper.prepare(cameraPos.x(), cameraPos.y(), cameraPos.z());
	}
	
	//public void renderAABB
}
*/