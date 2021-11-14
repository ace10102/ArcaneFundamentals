package com.Spoilers.arcanefundamentals.entities;

import com.Spoilers.arcanefundamentals.entities.renderers.FactionRaidHandlerEntityRenderer;
import com.Spoilers.arcanefundamentals.entities.renderers.UtilityEntityRenderer;

import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class AFEntityRenderers {
    @SubscribeEvent 
    @OnlyIn(value = Dist.CLIENT)
    public static void registerEntityRenderers(FMLClientSetupEvent event) {

        RenderingRegistry.registerEntityRenderingHandler((EntityType) (AFEntities.RITUAL_UTILITY_ENTITY.get()), UtilityEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler((EntityType) (AFEntities.FACTION_RAID_ENTITY.get()), FactionRaidHandlerEntityRenderer::new);

    }
}
