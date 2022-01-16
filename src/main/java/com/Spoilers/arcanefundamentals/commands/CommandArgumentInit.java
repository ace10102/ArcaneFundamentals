package com.Spoilers.arcanefundamentals.commands;

import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommandArgumentInit {
    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ArgumentTypes.register("arcanefundamentals:affinity_arg", AFAffinityArgument.class, new ArgumentSerializer<AFAffinityArgument>(AFAffinityArgument::new));
        });
    }
}

