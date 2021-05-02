package com.Spoilers.arcanefundamentals.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class AFClientConfig {
	
	public static ForgeConfigSpec.IntValue HUD_X;
	public static ForgeConfigSpec.IntValue HUD_Y;
	
	public static void init(ForgeConfigSpec.Builder clientConfig) {
		clientConfig.comment("Arcane Fundamentals // HUD Settings").push("af_hud_settings");
		HUD_X = clientConfig.comment(new String[]{"Positions relative to top left corner of the screen", "Large values may cause strange scaling", "Horizontal position of mana display"}).defineInRange("hud_x", 5, 0, 720);
		HUD_Y = clientConfig.comment("Vertical position of mana display").defineInRange("hud_y", 25, 0, 420);
	}
}
