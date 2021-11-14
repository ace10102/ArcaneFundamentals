package com.Spoilers.arcanefundamentals.rituals;

import java.util.Map;

import com.Spoilers.arcanefundamentals.ArcaneFundamentals;
import com.Spoilers.arcanefundamentals.entities.RitualUtilityEntity;
import com.ma.api.ManaAndArtificeMod;
import com.ma.api.affinity.Affinity;
import com.ma.api.capabilities.IPlayerMagic;
import com.ma.api.rituals.IRitualContext;
import com.ma.api.rituals.RitualEffect;
import com.ma.api.sound.SFX;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class RitualEffectCatharsis extends RitualEffect {

    public RitualEffectCatharsis(ResourceLocation ritualName) {
        super(ritualName);
    }

    @Override
    protected boolean applyRitualEffect(IRitualContext context) {
        PlayerEntity caster = context.getCaster();
        if (caster == null)
            return false;
        IPlayerMagic playerMagic = caster.getCapability(ManaAndArtificeMod.getMagicCapability()).orElse(null);
        if (playerMagic == null || !playerMagic.isMagicUnlocked())
            return false;
        
        Map<Affinity, Float> affinityList = playerMagic.getSortedAffinityDepths();
        Affinity primaryAffinity = getPrimaryAffinity(affinityList);
        
        context.getWorld().playSound(null, context.getCenter().getX(), context.getCenter().getY(),
                context.getCenter().getZ(), SFX.Spell.Cast.ForAffinity(primaryAffinity), SoundCategory.PLAYERS, 1.0f, (float) (0.9f + 0.2f * Math.random()));
        
        RitualUtilityEntity ritualHandler = new RitualUtilityEntity(context.getWorld(), this.getRegistryName().toString(), context.getCenter().asLong(), caster, 0);
        ritualHandler.setPos(context.getCenter().getX() + 0.5, context.getCenter().getY(), context.getCenter().getZ() + 0.5);
        ritualHandler.setAffinity(primaryAffinity);
        context.getWorld().addFreshEntity(ritualHandler);

        return true;
    }
    
    private Affinity getPrimaryAffinity(Map<Affinity, Float> affinityList) {
        Affinity highAffinity = Affinity.UNKNOWN;
        float highAffinityDepth = 0;
        for (Map.Entry<Affinity, Float> e : affinityList.entrySet()) {
            if (e.getValue()>= highAffinityDepth){
                highAffinityDepth = e.getValue();
                highAffinity = e.getKey();
            }
        } 
        return highAffinity;
    }

    @Override
    protected int getApplicationTicks(IRitualContext context) {
        return 0;
    }
    
    @Override
    protected boolean modifyRitualReagentsAndPatterns(ItemStack dataStack, IRitualContext context) { 
        PlayerEntity caster = context.getCaster();
        if (caster == null)
            return false;
        IPlayerMagic playerMagic = caster.getCapability(ManaAndArtificeMod.getMagicCapability()).orElse(null);
        if (playerMagic == null || !playerMagic.isMagicUnlocked())
            return false;
        Affinity primaryAffinity = getPrimaryAffinity(playerMagic.getSortedAffinityDepths());
        if (primaryAffinity == Affinity.UNKNOWN)
            return false;
        
        ResourceLocation replaceMote;
        ResourceLocation affinityItems;
        
        switch(primaryAffinity) {
        case ARCANE: 
            replaceMote = new ResourceLocation("mana-and-artifice:greater_mote_arcane");
            affinityItems = new ResourceLocation("arcanefundamentals:intermediate_ender");
            break;
        case EARTH: 
            replaceMote = new ResourceLocation("mana-and-artifice:greater_mote_earth");
            affinityItems = new ResourceLocation("arcanefundamentals:intermediate_wind");
            break;
        case ENDER: 
            replaceMote = new ResourceLocation("mana-and-artifice:greater_mote_ender");
            affinityItems = new ResourceLocation("arcanefundamentals:intermediate_arcane");
            break;
        case FIRE: 
            replaceMote = new ResourceLocation("mana-and-artifice:greater_mote_fire");
            affinityItems = new ResourceLocation("arcanefundamentals:intermediate_water");
            break;
        case WATER: 
            replaceMote = new ResourceLocation("mana-and-artifice:greater_mote_water");
            affinityItems = new ResourceLocation("arcanefundamentals:intermediate_fire");
            break;
        case WIND: 
            replaceMote = new ResourceLocation("mana-and-artifice:greater_mote_wind");
            affinityItems = new ResourceLocation("arcanefundamentals:intermediate_earth");
            break;
        
        default: return false;
        }
        
        context.replaceReagents(new ResourceLocation("arcanefundamentals:greater_motes"), replaceMote);
        context.replaceReagents(new ResourceLocation("arcanefundamentals:dynamic_affinity"), affinityItems);
        return true; 
        
    }
    
    @Override
    public ITextComponent canRitualStart(IRitualContext context) {
        BlockPos[] crystalPos = new BlockPos[] {context.getCenter().offset(2, 0, 2), context.getCenter().offset(2, 0, -2), context.getCenter().offset(-2, 0, 2), context.getCenter().offset(-2, 0, -2)};
        ITag<Block> afTags = BlockTags.getAllTags().getTagOrEmpty(new ResourceLocation("arcanefundamentals:chimerite_crystals"));
        for (BlockPos pos : crystalPos) {
            if(!context.getWorld().getBlockState(pos).getBlock().is(afTags)) {
                return new TranslationTextComponent("ritual.arcanefundamentals.ritual.no_crystal", pos);
            }
        }
        
        IPlayerMagic playerMagic = context.getCaster().getCapability(ManaAndArtificeMod.getMagicCapability()).orElse(null);
        if (playerMagic == null)
            return new TranslationTextComponent("error getting player magic");
        
        Affinity neededAffinity = getPrimaryAffinity(playerMagic.getSortedAffinityDepths()).getOpposite();
        NonNullList<Entity> entities= getNearbyAffinityEntities(context);
        if (entities.size() < 4) 
            return new TranslationTextComponent("ritual.arcanefundamentals.ritual.no_affinity", neededAffinity);
        for (Entity e: entities) {
            if (!neededAffinity.equals(getEntityAffinity(e, context)))
                return new TranslationTextComponent("ritual.arcanefundamentals.ritual.wrong_affinity", neededAffinity);
        }
        return null;
    }
    
    private NonNullList<Entity> getNearbyAffinityEntities(IRitualContext context) {
        int search_radius = 3;
        AxisAlignedBB bb = new AxisAlignedBB(context.getCenter()).inflate(search_radius);
        NonNullList<Entity> entities = NonNullList.create();
        for (Entity e : context.getWorld().getEntities(null, bb)) {
            if (e.getType().getRegistryName().toString().equals("mana-and-artifice:affinity_icon_entity")) {
                entities.add(e);
            }
        }
        return entities;
    }
    
    public Affinity getEntityAffinity(Entity entity, IRitualContext context) {
        CompoundNBT nbt = entity.serializeNBT();
        if (nbt.contains("affinity")) {        
            return Affinity.valueOf(nbt.getString("affinity"));
        }
        else {
            ArcaneFundamentals.LOGGER.error("Failed to load affinity for Affinity Icon Entity");
            return Affinity.UNKNOWN;
        } 
    }
}