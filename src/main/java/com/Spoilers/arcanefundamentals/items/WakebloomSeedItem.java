package com.Spoilers.arcanefundamentals.items;

import com.Spoilers.arcanefundamentals.util.RegistryHandler;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;

public class WakebloomSeedItem extends BlockItem {

	public WakebloomSeedItem(Block blockIn, Item.Properties builder) {
		super(blockIn, builder);
	}

	public ActionResultType onItemUse(ItemUseContext context) {
	      return ActionResultType.PASS;
	   }

	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
	      BlockRayTraceResult blockraytraceresult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);
	      BlockRayTraceResult blockraytraceresult1 = blockraytraceresult.withPosition(blockraytraceresult.getPos().up());
	      ActionResultType actionresulttype = super.onItemUse(new ItemUseContext(playerIn, handIn, blockraytraceresult1));
	      return new ActionResult<>(actionresulttype, playerIn.getHeldItem(handIn));
	}
	
	//to mess with in the future for ideal placement functionality
	/*public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        BlockRayTraceResult raytraceresult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);
        if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
            return ActionResult.resultPass(itemstack);
        }
        if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult blockraytraceresult = raytraceresult;
            BlockPos blockpostarget = blockraytraceresult.getPos();
            Direction direction = blockraytraceresult.getFace();
            if (!worldIn.isBlockModifiable(playerIn, blockpostarget) || !playerIn.canPlayerEdit(blockpostarget.offset(direction), direction, itemstack)) {
                return ActionResult.resultFail(itemstack);
            }
            BlockPos blockposcrop = blockpostarget.up();
            BlockState blockstate = worldIn.getBlockState(blockpostarget);
            Material material = blockstate.getMaterial();
            FluidState ifluidstate = worldIn.getFluidState(blockpostarget);
            if ((ifluidstate.getFluid() == Fluids.WATER || material == Material.ICE) && worldIn.isAirBlock(blockposcrop)) {
                BlockSnapshot blocksnapshot = BlockSnapshot.create((RegistryKey<World>)worldIn.getDimensionKey(), (IWorld)worldIn, (BlockPos)blockposcrop);
                worldIn.setBlockState(blockposcrop, (RegistryHandler.WAKEBLOOM_CROP.get()).getDefaultState(), 11);
                if (ForgeEventFactory.onBlockPlace((Entity)playerIn, (BlockSnapshot)blocksnapshot, (Direction)Direction.UP)) {
                    blocksnapshot.restore(true, false);
                    return ActionResult.resultFail(itemstack);
                }
                if (playerIn instanceof ServerPlayerEntity) {
                    CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)playerIn, blockposcrop, itemstack);
                }
                if (!playerIn.abilities.isCreativeMode) {
                    itemstack.shrink(1);
                }
                worldIn.playSound(playerIn, blockpostarget, SoundEvents.BLOCK_LILY_PAD_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                return ActionResult.resultSuccess(itemstack);
            }
        }
        return ActionResult.resultFail(itemstack);
    }*/
}
