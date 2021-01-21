package com.Spoilers.arcanefundamentals.blocks;

import com.Spoilers.arcanefundamentals.ArcaneFundamentals;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AFBlocks {
	
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ArcaneFundamentals.MOD_ID);
	/*Blocks*/
		//Crops
	public static final RegistryObject<Block> AUM_CROP = BLOCKS.register("aum_crop", () -> new AumCrop(Block.Properties.from(Blocks.WHEAT)));
	public static final RegistryObject<Block> CERUBLOSSOM_CROP = BLOCKS.register("cerublossom_crop", () -> new CerublossomCrop(Block.Properties.from(Blocks.WHEAT)));
	public static final RegistryObject<Block> DESERT_NOVA_CROP = BLOCKS.register("desert_nova_crop", () -> new DesertNovaCrop(Block.Properties.from(Blocks.WHEAT)));
	public static final RegistryObject<Block> TARMA_ROOT_CROP = BLOCKS.register("tarma_root_crop", () -> new TarmaRootCrop(Block.Properties.from(Blocks.WHEAT)));
	public static final RegistryObject<Block> WAKEBLOOM_CROP = BLOCKS.register("wakebloom_crop", () -> new WakebloomCrop(Block.Properties.from(Blocks.WHEAT)));
		//Building
	public static final RegistryObject<Block> VINTEUM_POWDER = BLOCKS.register("vinteum_powder", () -> new FallingBlock(Block.Properties.from(Blocks.SAND)));
}
