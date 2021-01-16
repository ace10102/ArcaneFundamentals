package com.Spoilers.arcanefundamentals.util;

import com.Spoilers.arcanefundamentals.ArcaneFundamentals;
import com.Spoilers.arcanefundamentals.blocks.AumCrop;
import com.Spoilers.arcanefundamentals.blocks.CerublossomCrop;
import com.Spoilers.arcanefundamentals.blocks.DesertNovaCrop;
import com.Spoilers.arcanefundamentals.blocks.TarmaRootCrop;
import com.Spoilers.arcanefundamentals.blocks.WakebloomCrop;
import com.Spoilers.arcanefundamentals.items.WakebloomSeedItem;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ArcaneFundamentals.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ArcaneFundamentals.MOD_ID);
    
    public static void init() {
        //Registers the deferredregisters
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
    
    public static final RegistryObject<Item> AUM_SEED = ITEMS.register("aum_seed", () -> new BlockItem(RegistryHandler.AUM_CROP.get(), new Item.Properties().group(ArcaneFundamentals.TAB)));
    public static final RegistryObject<Item> CERUBLOSSOM_SEED = ITEMS.register("cerublossom_seed", () -> new BlockItem(RegistryHandler.CERUBLOSSOM_CROP.get(), new Item.Properties().group(ArcaneFundamentals.TAB)));
    public static final RegistryObject<Item> DESERT_NOVA_SEED = ITEMS.register("desert_nova_seed", () -> new BlockItem(RegistryHandler.DESERT_NOVA_CROP.get(), new Item.Properties().group(ArcaneFundamentals.TAB)));
    public static final RegistryObject<Item> TARMA_ROOT_SEED = ITEMS.register("tarma_root_seed", () -> new BlockItem(RegistryHandler.TARMA_ROOT_CROP.get(), new Item.Properties().group(ArcaneFundamentals.TAB)));
    public static final RegistryObject<Item> WAKEBLOOM_SEED = ITEMS.register("wakebloom_seed", () -> new WakebloomSeedItem(RegistryHandler.WAKEBLOOM_CROP.get(), new Item.Properties().group(ArcaneFundamentals.TAB)));
   
    //Blocks
    public static final RegistryObject<Block> AUM_CROP = BLOCKS.register("aum_crop", () -> new AumCrop(Block.Properties.from(Blocks.WHEAT)));
    public static final RegistryObject<Block> CERUBLOSSOM_CROP = BLOCKS.register("cerublossom_crop", () -> new CerublossomCrop(Block.Properties.from(Blocks.WHEAT)));
    public static final RegistryObject<Block> DESERT_NOVA_CROP = BLOCKS.register("desert_nova_crop", () -> new DesertNovaCrop(Block.Properties.from(Blocks.WHEAT)));
    public static final RegistryObject<Block> TARMA_ROOT_CROP = BLOCKS.register("tarma_root_crop", () -> new TarmaRootCrop(Block.Properties.from(Blocks.WHEAT)));
    public static final RegistryObject<Block> WAKEBLOOM_CROP = BLOCKS.register("wakebloom_crop", () -> new WakebloomCrop(Block.Properties.from(Blocks.WHEAT)));
}
