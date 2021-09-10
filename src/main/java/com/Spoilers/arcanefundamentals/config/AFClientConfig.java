package com.Spoilers.arcanefundamentals.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class AFClientConfig {
	
	//Hud values
	public static ForgeConfigSpec.IntValue HUD_X;
	public static ForgeConfigSpec.IntValue HUD_Y;
	
	//Monocle element values
	public static ForgeConfigSpec.BooleanValue DISPLAY_SOULS;
	public static ForgeConfigSpec.IntValue ELEMENT_RENDER_DISTANCE;
	public static ForgeConfigSpec.DoubleValue ELEMENT_SIZE;
	public static ForgeConfigSpec.DoubleValue ELEMENT_HEIGHT;
	public static ForgeConfigSpec.DoubleValue ELEMENT_OFFSET;
	
	public static void init(ForgeConfigSpec.Builder clientConfig) {
		clientConfig.comment("Arcane Fundamentals // HUD Settings").push("af_hud_settings");
		HUD_X = clientConfig.comment(new String[]{"Positions relative to top left corner of the screen", "Large values may cause strange scaling", "Horizontal position of mana display"}).defineInRange("hud_x", 5, 0, 720);
		HUD_Y = clientConfig.comment("Vertical position of mana display").defineInRange("hud_y", 25, 0, 420);
		clientConfig.pop();
		/*
		clientConfig.comment("Arcane Fundamentals // Monocle Settings").push("af_monocle_settings");
		DISPLAY_SOULS = clientConfig.comment("Should the mana monocle display the souls of undead faction players").define("display_souls", true);
		ELEMENT_RENDER_DISTANCE = clientConfig.comment("Render distance for entity mana display").defineInRange("render_distance", 16, 8, 64);
		ELEMENT_SIZE = clientConfig.comment("Size of the mana display").defineInRange("display_size", 0.4, 0.0, 8.0);
		ELEMENT_HEIGHT = clientConfig.comment("Height of the mana display in blocks, relative to entity head").defineInRange("display_height", 0.22, -8.0, 8.0);
		ELEMENT_OFFSET = clientConfig.comment("Horizontal offset of the mana display in blocks, negative values move right").defineInRange("display_offset", 0.0, -8.0, 8.0);
		*/
	}
}
