package com.Spoilers.arcanefundamentals.util;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class CatchThrownRune {
    private static final ResourceLocation RuneProjectile = new ResourceLocation("mana-and-artifice:thrown_runescribing_pattern");

    @SubscribeEvent
    public void catchRune(ProjectileImpactEvent.Throwable event) {
        if (event.getEntity() != null && event.getRayTraceResult().getType() == Type.BLOCK) {
            World world = event.getEntity().level;
            ResourceLocation entity = event.getEntity().getType().getRegistryName();
            BlockPos hit = ((BlockRayTraceResult) event.getRayTraceResult()).getBlockPos();
            Block target = world.getBlockState(hit).getBlock();

            if (!world.isClientSide && entity.equals(RuneProjectile) && target == Blocks.CLAY) {
                Item RunePlate = event.getEntity().isOnFire() ? 
                        ForgeRegistries.ITEMS.getValue(new ResourceLocation("mana-and-artifice:rune_pattern")) : 
                        ForgeRegistries.ITEMS.getValue(new ResourceLocation("mana-and-artifice:rune_clay_plate"));
                world.destroyBlock(hit, false, event.getThrowable().getOwner());
                world.addFreshEntity(new ItemEntity(world, hit.getX(), hit.getY(), hit.getZ(), new ItemStack(RunePlate)));
            }
        }
    }
}
