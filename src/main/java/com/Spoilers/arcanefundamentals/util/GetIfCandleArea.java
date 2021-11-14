package com.Spoilers.arcanefundamentals.util;

import java.util.Comparator;
import java.util.Optional;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import com.Spoilers.arcanefundamentals.items.AFItems;
import com.ma.api.ManaAndArtificeMod;
import com.ma.api.capabilities.IWorldMagic;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;

public class GetIfCandleArea {

    @SubscribeEvent
    public void selectAndFilterBlock(RightClickBlock event) {

        World world = event.getWorld();
        IWorldMagic worldMagic = world.getCapability(ManaAndArtificeMod.getWorldMagicCapability()).orElse(null);
        Item usedItem = event.getItemStack().getItem();
        Optional<ImmutableTriple<String, Integer, ItemStack>> equipped = CuriosApi.getCuriosHelper().findEquippedCurio(AFItems.MANA_MONOCLE.get(), event.getPlayer());
        BlockPos targetBlock = event.getPos();

        if (world.isClientSide() || world.getBlockEntity(targetBlock) != null || !equipped.isPresent() || 
                !(equipped.get()).getRight().getItem().equals(AFItems.MANA_MONOCLE.get()) || 
                !usedItem.equals(Items.AIR) || worldMagic == null) {
            return;
        }
        
        if (event.getPlayer().isShiftKeyDown() && equipped.get().getRight().getTag() != null && equipped.get().getRight().getTag().contains("location")) {
            equipped.get().getRight().removeTagKey("location");
            return;
        }

        if (!event.getPlayer().isShiftKeyDown() && worldMagic.isWithinWardingCandle(targetBlock)) {
            long candle = worldMagic.getAllWardingCandleLocations().stream().map(e -> e).sorted(new Comparator<Long>() {

                @Override
                public int compare(Long o1, Long o2) {
                    Double d1 = BlockPos.of(o1).distSqr(targetBlock);
                    Double d2 = BlockPos.of(o2).distSqr(targetBlock);
                    return d1.compareTo(d2);
                }
            }).findFirst().get();

            equipped.get().getRight().getOrCreateTag().putLong("location", candle);
        }
    }
}
