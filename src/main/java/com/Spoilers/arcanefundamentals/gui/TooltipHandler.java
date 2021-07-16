package com.Spoilers.arcanefundamentals.gui;

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
import net.minecraftforge.registries.ForgeRegistries;

public class TooltipHandler {
	
	@SubscribeEvent @OnlyIn(value=Dist.CLIENT)
	public void ItemTooltipEvent(ItemTooltipEvent event) {
		final Item checkSpellBook = ForgeRegistries.ITEMS.getValue(new ResourceLocation("mana-and-artifice:spell_book"));
		final Item checkGrimoire = ForgeRegistries.ITEMS.getValue(new ResourceLocation("mana-and-artifice:grimoire"));
		final Item checkRoteBook = ForgeRegistries.ITEMS.getValue(new ResourceLocation("mana-and-artifice:book_of_rote"));
		
		final Item checkAlterBook = ForgeRegistries.ITEMS.getValue(new ResourceLocation("mana-and-artifice:modifier_book"));
		
		ItemStack hoveredStack = event.getItemStack();
		Item hoveredItem = hoveredStack.getItem();
		
		if (hoveredItem == checkSpellBook || hoveredItem == checkGrimoire || hoveredItem  == checkRoteBook) {
			
			addSpellBookTooltip(event.getToolTip());
		}
		if (hoveredItem == checkAlterBook) {
			
			addAlterBookTooltip(event.getToolTip());
		}
	}
	
	public void addSpellBookTooltip(List<ITextComponent> tooltip) {
		
		tooltip.add(new TranslationTextComponent("tooltip.keybind", KeyboardUtil.getSpellKey()).withStyle(TextFormatting.AQUA));	
		/*if (!KeyboardUtil.isCtrl()) {
        	tooltip.add(new TranslationTextComponent("tooltip.allbooksintro").mergeStyle(TextFormatting.AQUA));
        }*/
	}
	public void addAlterBookTooltip(List<ITextComponent> tooltip) {
		
		tooltip.add(new TranslationTextComponent("tooltip.alterbook", KeyboardUtil.getSpellKey()).withStyle(TextFormatting.AQUA));	
	}
}

