package com.Spoilers.arcanefundamentals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.Spoilers.arcanefundamentals.rituals.RitualEffectDecryptSpell;
import com.Spoilers.arcanefundamentals.util.RegistryHandler;

import com.ma.api.rituals.RitualEffect;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("arcanefundamentals")
public class ArcaneFundamentals {
	
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "arcanefundamentals";
	final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
	
	public ArcaneFundamentals() {
		
		/*FMLJavaModLoadingContext.get()).register(RegisterBook.onRegisterRituals);
		MinecraftForge.EVENT_BUS.register(RegisterBook.class :: onRegisterGuidebooks);*/
		modEventBus.addListener(this::doClientStuff);

        RegistryHandler.init();

	}
	
	private void doClientStuff(final FMLClientSetupEvent event) {
        RenderTypeLookup.setRenderLayer(RegistryHandler.DESERT_NOVA_CROP.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegistryHandler.TARMA_ROOT_CROP.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegistryHandler.WAKEBLOOM_CROP.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegistryHandler.AUM_CROP.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegistryHandler.CERUBLOSSOM_CROP.get(), RenderType.getCutout());
    }
	
	public static final ItemGroup TAB = new ItemGroup("arcaneFundamentalsTab") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(RegistryHandler.CERUBLOSSOM_SEED.get());
        }
    };
    
	@Mod.EventBusSubscriber(modid = ArcaneFundamentals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {
		
		@SubscribeEvent
		public static void onRegisterRituals(RegistryEvent.Register<RitualEffect> event) {
			event.getRegistry().register(new RitualEffectDecryptSpell(new ResourceLocation(ArcaneFundamentals.MOD_ID, "rituals/decrypt_spell"))
					.setRegistryName(new ResourceLocation(ArcaneFundamentals.MOD_ID, "ritual-effect-decrypt-spell")));
			ArcaneFundamentals.LOGGER.info("Arcane Fundamentals rituals registered");
		}
	}
}
