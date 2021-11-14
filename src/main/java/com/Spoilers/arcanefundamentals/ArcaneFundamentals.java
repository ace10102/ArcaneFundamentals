package com.Spoilers.arcanefundamentals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.Spoilers.arcanefundamentals.blocks.AFBlocks;
import com.Spoilers.arcanefundamentals.commands.CommandInit;
import com.Spoilers.arcanefundamentals.config.AFConfigInit;
import com.Spoilers.arcanefundamentals.entities.AFEntities;
import com.Spoilers.arcanefundamentals.entities.AFEntityRenderers;
import com.Spoilers.arcanefundamentals.gui.CandleRenderer;
//import com.Spoilers.arcanefundamentals.gui.HUDRenderer;
//import com.Spoilers.arcanefundamentals.gui.MonocleRenderer;
//import com.Spoilers.arcanefundamentals.gui.TooltipHandler;
import com.Spoilers.arcanefundamentals.items.AFItems;
import com.Spoilers.arcanefundamentals.rituals.RitualEffectCatharsis;
import com.Spoilers.arcanefundamentals.rituals.RitualEffectTreason;
import com.Spoilers.arcanefundamentals.rituals.RitualEffectUnspelling;
import com.Spoilers.arcanefundamentals.util.CatchThrownRune;
import com.Spoilers.arcanefundamentals.util.GetIfCandleArea;
import com.Spoilers.arcanefundamentals.util.WandCodexAlternate;
import com.ma.api.guidebook.RegisterGuidebooksEvent;
import com.ma.api.rituals.RitualEffect;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
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
        AFEntities.ENTITY_TYPES.register(this.modEventBus);

        MinecraftForge.EVENT_BUS.register(new WandCodexAlternate());
        MinecraftForge.EVENT_BUS.register(new CatchThrownRune());
        MinecraftForge.EVENT_BUS.register(new GetIfCandleArea());
        MinecraftForge.EVENT_BUS.register(this);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            MinecraftForge.EVENT_BUS.register(CommandInit.class);

            //MinecraftForge.EVENT_BUS.register(new HUDRenderer());
            MinecraftForge.EVENT_BUS.register(new CandleRenderer());
            // MinecraftForge.EVENT_BUS.register(new MonocleRenderer());
            // MinecraftForge.EVENT_BUS.register(new TooltipHandler());

            modEventBus.addListener(this::clientSetupStuff);
            modEventBus.register(AFEntityRenderers.class);
        });
    }

    @SubscribeEvent
    public void onRegisterGuidebooks(RegisterGuidebooksEvent event) {
        event.getRegistry().addGuidebookPath(new ResourceLocation(ArcaneFundamentals.MOD_ID, "guide"));
        ArcaneFundamentals.LOGGER.info("Arcane Fundamentals: guide registered");
    }

    private void clientSetupStuff(final FMLClientSetupEvent event) {
        RenderTypeLookup.setRenderLayer(AFBlocks.DESERT_NOVA_CROP.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(AFBlocks.TARMA_ROOT_CROP.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(AFBlocks.WAKEBLOOM_CROP.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(AFBlocks.AUM_CROP.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(AFBlocks.CERUBLOSSOM_CROP.get(), RenderType.cutout());
    }

    public static final ItemGroup TAB = new ItemGroup("arcaneFundamentalsTab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(AFItems.CERUBLOSSOM_SEED.get());
        }
    };

    @Mod.EventBusSubscriber(modid = ArcaneFundamentals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void onRegisterRituals(RegistryEvent.Register<RitualEffect> event) {
            event.getRegistry().register(new RitualEffectUnspelling(new ResourceLocation(ArcaneFundamentals.MOD_ID, "rituals/unspelling"))
                    .setRegistryName(new ResourceLocation(ArcaneFundamentals.MOD_ID, "ritual-effect-unspelling")));
            event.getRegistry().register(new RitualEffectTreason(new ResourceLocation(ArcaneFundamentals.MOD_ID, "rituals/treason"))
                    .setRegistryName(new ResourceLocation(ArcaneFundamentals.MOD_ID, "ritual-effect-treason")));
            event.getRegistry().register(new RitualEffectCatharsis(new ResourceLocation(ArcaneFundamentals.MOD_ID, "rituals/catharsis"))
                    .setRegistryName(new ResourceLocation(ArcaneFundamentals.MOD_ID, "ritual-effect-catharsis")));
            ArcaneFundamentals.LOGGER.info("Arcane Fundamentals: rituals registered");
        }
    }
}
