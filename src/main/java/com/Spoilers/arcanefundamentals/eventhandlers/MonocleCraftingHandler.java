package com.Spoilers.arcanefundamentals.eventhandlers;

import java.util.ArrayList;
import java.util.Arrays;

import com.Spoilers.arcanefundamentals.items.AFItems;
import com.ma.api.IEntityHelper;
import com.ma.api.ManaAndArtificeMod;
import com.ma.api.capabilities.IWorldMagic;
import com.ma.api.events.ManaweavePatternDrawnEvent;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class MonocleCraftingHandler{
    
    private static final ResourceLocation CIRCLE = new ResourceLocation("mana-and-artifice:manaweave_patterns/circle");
    private static final ResourceLocation DIAMOND = new ResourceLocation("mana-and-artifice:manaweave_patterns/diamond");
    private static final ResourceLocation STAR = new ResourceLocation("mana-and-artifice:manaweave_patterns/star");
    private static final ResourceLocation[] CANTRIP = {CIRCLE, DIAMOND, STAR};
    private static final ResourceLocation PEDESTAL = new ResourceLocation("mana-and-artifice:pedestal");
    
    @SubscribeEvent
    public void GetDrawEvent(ManaweavePatternDrawnEvent event) {
        World world = event.getCrafter().level;
        ResourceLocation drawnPattern = event.getPattern().getId();
        if(!Arrays.asList(CANTRIP).contains(drawnPattern)) 
            return;
        BlockPos origin = event.getCrafter().blockPosition();
        int radius = 3;
        AxisAlignedBB box = new AxisAlignedBB(origin).inflate(radius);
        for (Entity e : world.getEntities(event.getCrafter(), box)) {
           if (e.getType().getRegistryName().equals(new ResourceLocation("mana-and-artifice:manaweave_entity"))) {
               CompoundNBT entity = e.serializeNBT();
               if (entity.contains("patterns")) {
                   ArrayList<ResourceLocation> patterns = getPatterns((CompoundNBT)entity.get("patterns"));
                   patterns.add(drawnPattern);
                   if (patterns.size() == 3 && patterns.containsAll(Arrays.asList(CANTRIP))) {
                       BlockPos pedestal = getPedestal(origin, world, radius);
                       IWorldMagic worldMagic = world.getCapability(ManaAndArtificeMod.getWorldMagicCapability()).orElse(null);
                       if (worldMagic == null) 
                           return;
                       if (pedestal != null && worldMagic.getWellspringRegistry().getNodeAt(pedestal).isPresent()) {
                           IItemHandler contents = world.getBlockEntity(pedestal).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve().get();
                           contents.extractItem(0, 1, false);
                           IEntityHelper eh = ManaAndArtificeMod.getEntityHelper();
                           eh.createPresentItemEntity(world, pedestal.above().getX() + 0.5, pedestal.above().getY() + 0.5, pedestal.above().getZ() + 0.5, new ItemStack(AFItems.ELDRIN_MONOCLE.get()));
                       }
                   }
               }
           }
        }
    }
    
    private BlockPos getPedestal(BlockPos origin, World world, int radius) {
        BlockPos target = null;
        for (int y = -radius; y <= radius; ++y) {
            for (int x = -radius; x <= radius; ++x) {
                for (int z = -radius; z <= radius; ++z) {
                    target = origin.offset(x, y, z);
                    if (!world.getBlockState(target).getBlock().getRegistryName().equals(PEDESTAL)) continue;
                    IItemHandler contents = world.getBlockEntity(target).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent() ? 
                            world.getBlockEntity(target).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve().get() : null;
                    if (contents == null || contents.getStackInSlot(0).getItem() != AFItems.MANA_MONOCLE.get() ) continue;
                    return target;
                }
            }
        }
        return target;
    }
    
    private ArrayList<ResourceLocation> getPatterns(CompoundNBT nbt) {
        ArrayList<ResourceLocation> cachedPatterns = new ArrayList<ResourceLocation>();
        if (nbt.contains("count")) {
            int count = nbt.getInt("count");
            for (int i = 0; i <= count; ++i) {
                //String rLoc;
                ResourceLocation mwp;
                String key = "index_" + i;
                if (!nbt.contains(key) || (mwp = new ResourceLocation(nbt.getString(key))) == null) continue;
                cachedPatterns.add(0, mwp);
            } 
        }
        return cachedPatterns;
    }
}
