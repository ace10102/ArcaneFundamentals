package com.Spoilers.arcanefundamentals.gui;

import java.util.Optional;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.lwjgl.opengl.GL11;

import com.Spoilers.arcanefundamentals.ArcaneFundamentals;
import com.Spoilers.arcanefundamentals.config.AFClientConfig;
import com.Spoilers.arcanefundamentals.items.AFItems;
import com.ma.api.ManaAndArtificeMod;
import com.ma.api.capabilities.IPlayerMagic;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;

public class HUDRenderer extends AbstractGui {
	
	 private final Minecraft mc = Minecraft.getInstance();
	 
	 @SubscribeEvent(receiveCanceled=true, priority=EventPriority.LOW)
	 @OnlyIn(value=Dist.CLIENT)
	 public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
		 ClientPlayerEntity entityPlayerSP = mc.player;
		 if (entityPlayerSP == null) {
			 return;
		 }
		 if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
			 renderHUD(event.getMatrixStack(), event.getWindow().getScaledWidth(), event.getWindow().getScaledHeight());	 
		 }
	 }
	 
	 public void renderHUD(MatrixStack matrixStack, int screenWidth, int screenHeight) {
		 ClientPlayerEntity player = mc.player;
		 Optional<ImmutableTriple<String, Integer, ItemStack>> equipped = CuriosApi.getCuriosHelper().findEquippedCurio(AFItems.MANA_MONOCLE.get(), player);
		 if (!equipped.isPresent() || !((ImmutableTriple<String, Integer, ItemStack>)equipped.get()).getRight().getItem().equals(AFItems.MANA_MONOCLE.get())) {
			 return;
		 }
		 IPlayerMagic magic = (IPlayerMagic)player.getCapability(ManaAndArtificeMod.getMagicCapability()).orElse(null);
		 if (magic == null || !magic.isMagicUnlocked()) {
			 return;
		 }
		 float scaleFactor = 1.0f;
		 int xPos = AFClientConfig.HUD_X.get();
		 int yPos = AFClientConfig.HUD_Y.get();
		 GL11.glPushAttrib(1048575);
		 GL11.glPushMatrix();
		 GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);
		 mc.getTextureManager().bindTexture(new ResourceLocation(ArcaneFundamentals.MOD_ID, "textures/gui/mana_value_bar.png"));
		 renderManaValues(matrixStack, xPos, yPos, magic);
		 GL11.glPopMatrix();
		 GL11.glPopAttrib();
	 }
	 
	 @SuppressWarnings("deprecation")
	private void renderManaValues(MatrixStack matrixStack, int xPos, int yPos, IPlayerMagic magic) {
		 if (magic.getMaxMana() > 0.0f) {
			 float scale = 0.25f;
			 RenderSystem.pushMatrix();
			 RenderSystem.scalef(scale, scale/4, scale);
			 blit(matrixStack, xPos*4, yPos*16, 0, 0, 256, 256);
			 RenderSystem.popMatrix();
			 xPos += 11;
			 yPos += 4;
			 FontRenderer fontRender = Minecraft.getInstance().fontRenderer;
			 String manaDisplay = (int)magic.getMana() + "/" + (int)magic.getMaxMana();
			 if (mc.getForceUnicodeFont()) {
				 fontRender.drawString(matrixStack, manaDisplay, ((float)xPos + 7.5f), ((float)yPos - 0.5f), ColorHelper.PackedColor.packColor(255, 100, 0, 100));
			 } else {
				 fontRender.drawString(matrixStack, manaDisplay, ((float)xPos + 0.5f), ((float)yPos + 0.5f), ColorHelper.PackedColor.packColor(255, 100, 0, 100));
			 }
		 }
	 }
}
