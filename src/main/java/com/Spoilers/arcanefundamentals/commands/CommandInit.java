package com.Spoilers.arcanefundamentals.commands;

//import com.mojang.brigadier.CommandDispatcher;
//import net.minecraft.command.CommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommandInit {

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        CommandReplaceWellspring.register(event.getDispatcher());
        //CommandConfigureHUD.register(event.getDispatcher());
        //(CommandDispatcher<CommandSource>)
    }
}