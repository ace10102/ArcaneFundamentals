package com.Spoilers.arcanefundamentals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.Spoilers.arcanefundamentals.blocks.AFBlocks;
import com.Spoilers.arcanefundamentals.config.AFConfigInit;
import com.Spoilers.arcanefundamentals.gui.HUDRenderer;
import com.Spoilers.arcanefundamentals.items.AFItems;
import com.Spoilers.arcanefundamentals.rituals.RitualEffectUnspell;
//import com.Spoilers.arcanefundamentals.util.RegistryHandler;
import com.ma.api.guidebook.RegisterGuidebooksEvent;
import com.ma.api.rituals.RitualEffect;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod("arcanefundamentals")
public class ArcaneFundamentals {
	
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "arcanefundamentals";
	final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
	
	public ArcaneFundamentals() {
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, AFConfigInit.SERVER_CONFIG);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, AFConfigInit.CLIENT_CONFIG);
		
		AFConfigInit.loadConfig(AFConfigInit.SERVER_CONFIG, FMLPaths.CONFIGDIR.get().resolve("arcanefundamentals-server.toml"));
		AFConfigInit.loadConfig(AFConfigInit.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve("arcanefundamentals-client.toml"));
		
		AFItems.ITEMS.register(this.modEventBus);
        AFBlocks.BLOCKS.register(this.modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(HUDRenderer.class);
        
        modEventBus.addListener(this::clientSetupStuff);
	}
	
	@SubscribeEvent
    public void onRegisterGuidebooks(RegisterGuidebooksEvent event) {
        event.getRegistry().addGuidebookPath(new ResourceLocation(ArcaneFundamentals.MOD_ID, "guide"));
        ArcaneFundamentals.LOGGER.info("Arcane Fundamentals: guide registered");
    }
	
	private void clientSetupStuff(final FMLClientSetupEvent event) {
        RenderTypeLookup.setRenderLayer(AFBlocks.DESERT_NOVA_CROP.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(AFBlocks.TARMA_ROOT_CROP.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(AFBlocks.WAKEBLOOM_CROP.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(AFBlocks.AUM_CROP.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(AFBlocks.CERUBLOSSOM_CROP.get(), RenderType.getCutout());
        
        HUDRenderer.instance = new HUDRenderer();
    }
	
	public static final ItemGroup TAB = new ItemGroup("arcaneFundamentalsTab") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(AFItems.CERUBLOSSOM_SEED.get());
		}
    };
    
	@Mod.EventBusSubscriber(modid = ArcaneFundamentals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {
		
		@SubscribeEvent
		public static void onRegisterRituals(RegistryEvent.Register<RitualEffect> event) {
			event.getRegistry().register(new RitualEffectUnspell(new ResourceLocation(ArcaneFundamentals.MOD_ID, "rituals/unspell"))
					.setRegistryName(new ResourceLocation(ArcaneFundamentals.MOD_ID, "ritual-effect-unspelling")));
			ArcaneFundamentals.LOGGER.info("Arcane Fundamentals: rituals registered");
		}
	}
}
