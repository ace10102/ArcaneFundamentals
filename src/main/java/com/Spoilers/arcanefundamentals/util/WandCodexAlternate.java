package com.Spoilers.arcanefundamentals.util;

import java.util.Random;

import com.Spoilers.arcanefundamentals.config.AFServerConfig;
import com.Spoilers.arcanefundamentals.particle.AFParticleType;
import com.Spoilers.arcanefundamentals.particle.ParticleGetter;

import net.minecraft.block.Block;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
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
        final Item checkAWand = ForgeRegistries.ITEMS.getValue(new ResourceLocation("mana-and-artifice:manaweaver_wand_advanced"));
        final Item codex = ForgeRegistries.ITEMS.getValue(new ResourceLocation("mana-and-artifice:guide_book"));
        final Block flower = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("mana-and-artifice:cerublossom"));

        World world = event.getWorld();
        Item usedItem = event.getItemStack().getItem();

        if (AFServerConfig.enableCodexAlternate.get() && (usedItem == checkWand || usedItem == checkIWand || usedItem == checkAWand) && 
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
        if (!world.isClientSide) {
            return;
        }
        int i;
        Random rnd = new Random();
        Vector3f srcPoint = new Vector3f(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f);
        ParticleType<?> instanceSparkle = AFParticleType.getCheckedInstance(ParticleGetter.getParticle(ParticleGetter.BLUE_SPARKLE_GRAVITY));
        if (instanceSparkle != null) { 
            for (i = 0; i < 150; ++i) {
                world.addParticle((IParticleData)instanceSparkle, srcPoint.x(), srcPoint.y(), srcPoint.z(), -0.5 + rnd.nextFloat(), 0.04, -0.5 + rnd.nextFloat());
            }
        }
        for (i = 0; i < 75; ++i) {
            world.addParticle(ParticleTypes.ENCHANT, srcPoint.x(), srcPoint.y(), srcPoint.z(), -2.5 + (rnd.nextFloat() * 5), 0.5, -2.5 + (rnd.nextFloat() * 5));
        }
        ParticleType<?> instanceLight = AFParticleType.getCheckedInstance(ParticleGetter.getParticle(ParticleGetter.LIGHT_VELOCITY));
        if (instanceLight != null) { 
            for (i = 0; i < 50; ++i) {
                Vector3f lightPoint = new Vector3f(srcPoint.x() - 0.2f + rnd.nextFloat() * 0.4f, srcPoint.y() - 0.4f, srcPoint.z() - 0.2f + rnd.nextFloat() * 0.4f);
                world.addParticle((IParticleData)instanceLight, lightPoint.x(), lightPoint.y(), lightPoint.z(), 0, 0.02, 0);
            }
        }
    }
}
