package com.Spoilers.arcanefundamentals.util;

import java.util.Random;

import com.Spoilers.arcanefundamentals.config.AFServerConfig;
import com.ma.api.particles.ParticleInit;

import net.minecraft.block.Block;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class WandCodexAlternate {
	
	@SubscribeEvent
	public void onWandUse(EntityInteract event) {
		
		final Item checkWand = ForgeRegistries.ITEMS.getValue(new ResourceLocation("mana-and-artifice:manaweaver_wand"));
		final Item checkIWand = ForgeRegistries.ITEMS.getValue(new ResourceLocation("mana-and-artifice:improvised_manaweaver_wand"));
		final Item codex = ForgeRegistries.ITEMS.getValue(new ResourceLocation("mana-and-artifice:guide_book"));
		final Block flower = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("mana-and-artifice:cerublossom"));
		
		World world = event.getWorld();
		Item usedItem = event.getItemStack().getItem();
		
		if (AFServerConfig.enableCodexAlternate.get() && (usedItem == checkWand || usedItem == checkIWand) && 
				event.getTarget() instanceof ItemFrameEntity && ((ItemFrameEntity)event.getTarget()).getItem().getItem() == Items.BOOK && 
				world.getBlockState(event.getTarget().blockPosition()).getBlock() == flower) {
			
			if (!world.isClientSide) {
				
				world.destroyBlock(event.getTarget().blockPosition(), false, event.getPlayer());
				((ItemFrameEntity)event.getTarget()).setItem(new ItemStack(codex));
				
				event.setCanceled(true);
			}
			
			if (world.isClientSide) {
				spawnCodexCreationParticles(world, event.getTarget().blockPosition());
				event.setCancellationResult(ActionResultType.SUCCESS);
				event.setCanceled(true);
			}
		}
	}
	
	public void spawnCodexCreationParticles(World world, BlockPos pos) {
        int i;
        if (!world.isClientSide) {
            return;
        }
        Random rnd = new Random();
        Vector3f srcPoint = new Vector3f(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f);
        for (i = 0; i < 150; ++i) {
            world.addParticle(ParticleInit.BLUE_SPARKLE_GRAVITY.get(), srcPoint.x(), srcPoint.y(), srcPoint.z(), -0.5 + rnd.nextFloat(), 0.04, -0.5 + rnd.nextFloat());
        }
        for (i = 0; i < 75; ++i) {
            world.addParticle(ParticleTypes.ENCHANT, srcPoint.x(), srcPoint.y(), srcPoint.z(), -2.5 + (rnd.nextFloat() * 5), 0.5, -2.5 + (rnd.nextFloat() * 5));
        }
        for (i = 0; i < 50; ++i) {
            Vector3f lightPoint = new Vector3f(srcPoint.x() - 0.2f + rnd.nextFloat() * 0.4f, srcPoint.y() - 0.4f, srcPoint.z() - 0.2f + rnd.nextFloat() * 0.4f);
            world.addParticle(ParticleInit.LIGHT_VELOCITY.get(), lightPoint.x(), lightPoint.y(), lightPoint.z(), 0, 0.02, 0);
        }
    }
}
