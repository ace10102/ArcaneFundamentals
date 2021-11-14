/*package com.Spoilers.arcanefundamentals.util;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class KeyboardUtil {

    private static final Minecraft mc = Minecraft.getInstance();
    private static final long MINECRAFT_WINDOW = mc.getWindow().getWindow();

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
        for (KeyBinding iterKey : mc.options.keyMappings) {
            if (iterKey.getName().equals("key.spellbookopen")) {
                spellKeyValue = KeyBinding.createNameSupplier(iterKey.getName()).get().getString();
            } 
            else continue;
        }
        return spellKeyValue.equals("") ? spellKeyValue = "Key Unbound" : "Hold " + "'" + spellKeyValue + "'";
    }
}*/
