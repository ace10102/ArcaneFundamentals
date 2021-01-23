package com.Spoilers.arcanefundamentals.gui;

import org.lwjgl.opengl.GL11;

import com.ma.api.capabilities.IPlayerMagic;
import com.ma.capabilities.playerdata.magic.PlayerMagicProvider;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HUDRenderer extends AbstractGui {
	
	 private Minecraft mc = Minecraft.getInstance();
	 public static HUDRenderer instance;
	 
	 @SubscribeEvent(receiveCanceled=true, priority=EventPriority.LOW)
	 @OnlyIn(value=Dist.CLIENT)
	 public static void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
		 ClientPlayerEntity entityPlayerSP = HUDRenderer.instance.mc.player;
		 if (entityPlayerSP == null) {
			 return;
		 }
		 if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
			 instance.renderHUD(event.getMatrixStack(), event.getWindow().getScaledWidth(), event.getWindow().getScaledHeight());	 
		 }
	 }
	 public void renderHUD(MatrixStack matrixStack, int screenWidth, int screenHeight) {
		 ClientPlayerEntity player = this.mc.player;
		 IPlayerMagic magic = (IPlayerMagic)player.getCapability(PlayerMagicProvider.MAGIC).orElse(null);
		 if (magic == null || !magic.isMagicUnlocked()) {
			 return;
		 }
		 float scaleFactor = 1.0f;
		 int yPos = 25;
		 int xPos = 5;
		 GL11.glPushAttrib(1048575);
		 GL11.glPushMatrix();
		 GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);
		 this.mc.getTextureManager().bindTexture(new ResourceLocation("arcanefundamentals", "textures/gui/mana_value_bar.png"));
		 this.renderManaValues(matrixStack, xPos, yPos, magic);
		 GL11.glPopMatrix();
		 GL11.glPopAttrib();
	 }
	 
	 private void renderManaValues(MatrixStack matrixStack, int xPos, int yPos, IPlayerMagic magic) {
		 if (magic.getMaxMana() > 0.0f) {
			 float scale = 0.25f;
			 RenderSystem.pushMatrix();
			 RenderSystem.scalef(scale, scale/4, scale);
			 this.blit(matrixStack, xPos*4, yPos*16, 0, 0, 256, 256);
			 RenderSystem.popMatrix();
			 xPos += 11;
			 yPos += 4;
			 FontRenderer fontRender = Minecraft.getInstance().fontRenderer;
			 String manaDisplay = (int)magic.getMana() + "/" + (int)magic.getMaxMana();
			 if (this.mc.getForceUnicodeFont()) {
				 fontRender.drawString(matrixStack, manaDisplay, ((float)xPos + 7.5f), ((float)yPos - 0.5f), ColorHelper.PackedColor.packColor(255, 100, 0, 100));
			 } else {
				 fontRender.drawString(matrixStack, manaDisplay, ((float)xPos + 0.5f), ((float)yPos + 0.5f), ColorHelper.PackedColor.packColor(255, 100, 0, 100));
			 }
		 }
	 }
}
