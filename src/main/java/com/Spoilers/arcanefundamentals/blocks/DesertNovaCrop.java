package com.Spoilers.arcanefundamentals.blocks;

import com.Spoilers.arcanefundamentals.items.AFItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

public class DesertNovaCrop extends CropsBlock {

    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[] { Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D) };

    public DesertNovaCrop(Properties builder) {
        super(builder);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE_BY_AGE[state.getValue(this.getAgeProperty())];
    }

    @Override
    public boolean mayPlaceOn(BlockState groundBlock, IBlockReader worldIn, BlockPos pos) {
        ITag<Block> afTags = BlockTags.getAllTags().getTagOrEmpty(new ResourceLocation("arcanefundamentals:desert_nova_growable"));
        if (afTags != null) {
            return afTags.contains(groundBlock.getBlock());
        }
        return false;
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockState groundState = worldIn.getBlockState(pos.below());
        return (worldIn.getRawBrightness(pos, 0) >= 8 || worldIn.canSeeSky(pos)) && (this.mayPlaceOn(groundState, worldIn, pos));
    }

    @Override
    protected IItemProvider getBaseSeedId() {
        return AFItems.DESERT_NOVA_SEED.get();
    }
}
