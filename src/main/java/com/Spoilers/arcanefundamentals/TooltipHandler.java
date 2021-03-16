package com.Spoilers.arcanefundamentals;

import java.util.List;

import com.Spoilers.arcanefundamentals.util.KeyboardUtil;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = ArcaneFundamentals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TooltipHandler {
	
	@SubscribeEvent @OnlyIn(value=Dist.CLIENT)
	public static void ItemTooltipEvent(ItemTooltipEvent event) {
		final Item checkSpellBook = ForgeRegistries.ITEMS.getValue(new ResourceLocation("mana-and-artifice:spell_book"));
		final Item checkGrimoire = ForgeRegistries.ITEMS.getValue(new ResourceLocation("mana-and-artifice:grimoire"));
		final Item checkRoteBook = ForgeRegistries.ITEMS.getValue(new ResourceLocation("mana-and-artifice:book_of_rote"));
		
		ItemStack hoveredItem = event.getItemStack();
		
		if (hoveredItem.getItem() == checkSpellBook || hoveredItem.getItem() == checkGrimoire || hoveredItem.getItem() == checkRoteBook) {
			
			addSpellBookInformation(hoveredItem, event.getToolTip());	
		}
	}
	
	public static void addSpellBookInformation(ItemStack stack, List<ITextComponent> tooltip) {
		
		/*if (!KeyboardUtil.isCtrl()) {
        	tooltip.add(new TranslationTextComponent("tooltip.allbooksintro").mergeStyle(TextFormatting.AQUA));
        }*/
		tooltip.add(new TranslationTextComponent("tooltip.keybind", KeyboardUtil.getSpellKey()).mergeStyle(TextFormatting.AQUA));
	}
}

