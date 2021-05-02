package com.Spoilers.arcanefundamentals.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class AFServerConfig {
	
	public static ForgeConfigSpec.BooleanValue enableCodexAlternate;
	
	public static void init(ForgeConfigSpec.Builder serverConfig) {
		
		serverConfig.comment("Arcane Fundamentals // Server Settings").push("af_server_settings");
		enableCodexAlternate = serverConfig.comment("Enable to allow creating a Codex Arcana by using a manaweaveing wand on an item frame containing a book.").define("codex_alternate", false);
	}
}
