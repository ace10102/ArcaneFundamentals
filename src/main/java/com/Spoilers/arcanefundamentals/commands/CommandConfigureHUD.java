package com.Spoilers.arcanefundamentals.commands;

import com.Spoilers.arcanefundamentals.config.AFClientConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class CommandConfigureHUD {
	
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("afuicfg")
        		.requires(commandSource -> commandSource.hasPermissionLevel(0))
        		.then(Commands.argument("xpos", IntegerArgumentType.integer(0, 720))
        		.then(Commands.argument("ypos", IntegerArgumentType.integer(0, 420))
        		.executes(CommandConfigureHUD::changeHudPos))));
    }
	static int changeHudPos(CommandContext<CommandSource> commandContext) {
		int changedXpos = IntegerArgumentType.getInteger(commandContext, "xpos");
		int changedYpos = IntegerArgumentType.getInteger(commandContext, "ypos");
		AFClientConfig.HUD_X.set(changedXpos);
		AFClientConfig.HUD_Y.set(changedYpos);
		return 1;
	}
	
}
