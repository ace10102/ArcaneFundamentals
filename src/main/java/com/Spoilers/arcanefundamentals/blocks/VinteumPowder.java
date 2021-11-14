package com.Spoilers.arcanefundamentals.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VinteumPowder extends FallingBlock {

    public VinteumPowder(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    public int getDustColor(BlockState state, IBlockReader reader, BlockPos pos) {
        return -8356741;
    }
}
