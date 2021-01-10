package com.Spoilers.arcanefundamentals.blocks;

import com.Spoilers.arcanefundamentals.ArcaneFundamentals;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class BlockItemBase extends BlockItem {

    public BlockItemBase(Block blockIn) {
        super(blockIn, new Properties().group(ArcaneFundamentals.TAB));

    }
}
