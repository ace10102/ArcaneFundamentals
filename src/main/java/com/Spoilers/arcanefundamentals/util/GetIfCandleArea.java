/*package com.Spoilers.arcanefundamentals.util;

import java.util.Optional;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import com.Spoilers.arcanefundamentals.items.AFItems;
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
		Item usedItem = event.getItemStack().getItem();
		Optional<ImmutableTriple<String, Integer, ItemStack>> equipped = CuriosApi.getCuriosHelper().findEquippedCurio(AFItems.MANA_MONOCLE.get(), event.getPlayer());
		BlockPos targetBlock = event.getPos();
		
		if (world.isClientSide() || 
				!equipped.isPresent() || 
				!((ImmutableTriple<String, Integer, ItemStack>)equipped.get()).getRight().getItem().equals(AFItems.MANA_MONOCLE.get()) ||
				!usedItem.equals(Items.AIR)) {
			return;
		}
		
		//boolean test = IWorldMagic.isWithinWardingCandle(targetBlock);
		
		//System.out.println(event.getItemStack() +""+ targetBlock);
	}

}
*/