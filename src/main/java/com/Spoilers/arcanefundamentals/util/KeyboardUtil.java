package com.Spoilers.arcanefundamentals.util;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class KeyboardUtil {
	
	private static final long MINECRAFT_WINDOW = Minecraft.getInstance().getMainWindow().getHandle();
	
	@OnlyIn(Dist.CLIENT)
	public static boolean isShift() {
		return InputMappings.isKeyDown(MINECRAFT_WINDOW, GLFW.GLFW_KEY_LEFT_SHIFT) || InputMappings.isKeyDown(MINECRAFT_WINDOW, GLFW.GLFW_KEY_RIGHT_SHIFT);
	}
	@OnlyIn(Dist.CLIENT)
	public static boolean isCtrl() {
		return InputMappings.isKeyDown(MINECRAFT_WINDOW, GLFW.GLFW_KEY_LEFT_CONTROL) || InputMappings.isKeyDown(MINECRAFT_WINDOW, GLFW.GLFW_KEY_RIGHT_CONTROL);
	}
	
	public static String getSpellKey() {
		String spellKeyValue = null;
		for (KeyBinding iterKey : Minecraft.getInstance().gameSettings.keyBindings) {
			if (iterKey.getKeyDescription().equals("key.spellbookopen")) {
				spellKeyValue = KeyBinding.getDisplayString(iterKey.getKeyDescription()).get().getString();
			}
			else continue;
		}
		return spellKeyValue.equals("") ?  spellKeyValue = "Key Unbound" : "Hold " + "'" + spellKeyValue + "'";
	}
}
