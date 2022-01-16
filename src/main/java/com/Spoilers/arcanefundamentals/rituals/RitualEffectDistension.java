package com.Spoilers.arcanefundamentals.rituals;

import com.ma.api.ManaAndArtificeMod;
import com.ma.api.capabilities.IWellspringNodeRegistry;
import com.ma.api.capabilities.IWorldMagic;
import com.ma.api.capabilities.WellspringNode;
import com.ma.api.rituals.IRitualContext;
import com.ma.api.rituals.RitualEffect;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class RitualEffectDistension extends RitualEffect {

    public RitualEffectDistension(ResourceLocation ritualName) {
        super(ritualName);
    }

    @Override
    protected boolean applyRitualEffect(IRitualContext context) {
        IWorldMagic worldMagic = context.getWorld().getCapability(ManaAndArtificeMod.getWorldMagicCapability()).orElse(null);
        if (worldMagic == null)
            return false;
        IWellspringNodeRegistry wellspringRegistry = worldMagic.getWellspringRegistry();
        WellspringNode thisWellspring = wellspringRegistry.getNodeAt(context.getCenter()).isPresent() ? wellspringRegistry.getNodeAt(context.getCenter()).get() : null;
        if (thisWellspring == null || thisWellspring.getStrength() >= 10)
            return false;
        BlockPos[] pedestalPos = new BlockPos[] {context.getCenter().offset(2, 0, 2), context.getCenter().offset(2, 0, -2), context.getCenter().offset(-2, 0, 2), context.getCenter().offset(-2, 0, -2)};
        Block pedestal = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("mana-and-artifice:pedestal"));
        for (BlockPos pos : pedestalPos) {
            if (!context.getWorld().getBlockState(pos).getBlock().equals(pedestal)) {
                return false;
            }
            IItemHandler contents = context.getWorld().getBlockEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent() ? 
                    context.getWorld().getBlockEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve().get() : null;
            Item mote = moteForWellspring(thisWellspring);
            if (contents == null || contents.getStackInSlot(0).getItem() != mote ) {
                return false;
            }
            contents.extractItem(0, 1, false);
        }
        
        CompoundNBT oldWellspring = new CompoundNBT();
        thisWellspring.writeToNBT(oldWellspring);
        
        oldWellspring.putFloat("strength", thisWellspring.getStrength() + 5F);
        
        WellspringNode newWellspring = WellspringNode.fromNBT(oldWellspring);
        wellspringRegistry.addNode(context.getCenter(), newWellspring, true);
        
        return true;
    }

    @Override
    protected int getApplicationTicks(IRitualContext context) {
        return 0;
    }
    
    @Override
    public ITextComponent canRitualStart(IRitualContext context) {
        IWorldMagic worldMagic = context.getWorld().getCapability(ManaAndArtificeMod.getWorldMagicCapability()).orElse(null);
        if (worldMagic == null)
            return new TranslationTextComponent("general.arcanefundamentals.world_error");
        IWellspringNodeRegistry wellspringRegistry = worldMagic.getWellspringRegistry();
        WellspringNode thisWellspring = wellspringRegistry.getNodeAt(context.getCenter()).isPresent() ? wellspringRegistry.getNodeAt(context.getCenter()).get() : null;
        if (thisWellspring == null)
            return new TranslationTextComponent("general.arcanefundamentals.no_wellspring", context.getCenter());
        if (thisWellspring.getStrength() >= 10)
            return new TranslationTextComponent("ritual.arcanefundamentals.strong_wellspring");
        BlockPos[] pedestalPos = new BlockPos[] {context.getCenter().offset(2, 0, 2), context.getCenter().offset(2, 0, -2), context.getCenter().offset(-2, 0, 2), context.getCenter().offset(-2, 0, -2)};
        Block pedestal = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("mana-and-artifice:pedestal"));
        for (BlockPos pos : pedestalPos) {
            if (!context.getWorld().getBlockState(pos).getBlock().equals(pedestal)) {
                return new TranslationTextComponent("ritual.arcanefundamentals.no_pedestal", pos.toShortString());
            }
        }
        for (BlockPos pos :pedestalPos) {
            IItemHandler contents = context.getWorld().getBlockEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent() ? 
                    context.getWorld().getBlockEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve().get() : null;
            Item mote = moteForWellspring(thisWellspring);
            if (contents == null || contents.getStackInSlot(0).getItem() != mote ) {
                IFormattableTextComponent itemName = new TranslationTextComponent(mote.getDescription().getString()).withStyle(TextFormatting.AQUA);
                return new TranslationTextComponent("ritual.arcanefundamentals.wrong_item", pos.toShortString(), itemName);
            }
        }
        return null;
    }
    
    private Item moteForWellspring(WellspringNode node) {
        ResourceLocation moteRloc;
        switch(node.getAffinity().getShiftAffinity()) {
        case ARCANE: 
            moteRloc = new ResourceLocation("mana-and-artifice:mote_arcane");
            break;
        case EARTH: 
            moteRloc = new ResourceLocation("mana-and-artifice:mote_earth");
            break;
        case ENDER: 
            moteRloc = new ResourceLocation("mana-and-artifice:mote_ender");
            break;
        case FIRE: 
            moteRloc = new ResourceLocation("mana-and-artifice:mote_fire");
            break;
        case WATER: 
            moteRloc = new ResourceLocation("mana-and-artifice:mote_water");
            break;
        case WIND: 
            moteRloc = new ResourceLocation("mana-and-artifice:mote_wind");
            break;
        default: moteRloc = new ResourceLocation("minecraft:bedrock");
        }
        return ForgeRegistries.ITEMS.getValue(moteRloc);
    }
}       