package com.Spoilers.arcanefundamentals.items;

import com.Spoilers.arcanefundamentals.ArcaneFundamentals;
import com.Spoilers.arcanefundamentals.blocks.AFBlocks;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AFItems {
	
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ArcaneFundamentals.MOD_ID);
	
	/*Items*/
		//Seeds
	public static final RegistryObject<Item> AUM_SEED = ITEMS.register("aum_seed", () -> new BlockItem(AFBlocks.AUM_CROP.get(), new Item.Properties().group(ArcaneFundamentals.TAB)));
	public static final RegistryObject<Item> CERUBLOSSOM_SEED = ITEMS.register("cerublossom_seed", () -> new BlockItem(AFBlocks.CERUBLOSSOM_CROP.get(), new Item.Properties().group(ArcaneFundamentals.TAB)));
	public static final RegistryObject<Item> DESERT_NOVA_SEED = ITEMS.register("desert_nova_seed", () -> new BlockItem(AFBlocks.DESERT_NOVA_CROP.get(), new Item.Properties().group(ArcaneFundamentals.TAB)));
	public static final RegistryObject<Item> TARMA_ROOT_SEED = ITEMS.register("tarma_root_seed", () -> new BlockItem(AFBlocks.TARMA_ROOT_CROP.get(), new Item.Properties().group(ArcaneFundamentals.TAB)));
	public static final RegistryObject<Item> WAKEBLOOM_SEED = ITEMS.register("wakebloom_seed", () -> new WakebloomSeedItem(AFBlocks.WAKEBLOOM_CROP.get(), new Item.Properties().group(ArcaneFundamentals.TAB)));
		//Other
	public static final RegistryObject<Item> VINTEUM_POWDER = ITEMS.register("vinteum_powder", () -> new BlockItem(AFBlocks.VINTEUM_POWDER.get(), new Item.Properties().group(ArcaneFundamentals.TAB)));
	public static final RegistryObject<Item> MANA_MONOCLE = ITEMS.register("mana_monocle", () -> new Item(new Item.Properties().group(ArcaneFundamentals.TAB).maxStackSize(1)));
}
