package com.Spoilers.arcanefundamentals.rituals;

//import java.lang.reflect.Method;
import java.util.Collections;

import com.ma.api.ManaAndArtificeMod;
import com.ma.api.affinity.Affinity;
import com.ma.api.rituals.IRitualContext;
import com.ma.api.rituals.RitualEffect;
import com.ma.api.sound.SFX;
/*import com.ma.entities.utility.EntityPresentItem;*/
import com.ma.spells.crafting.SpellRecipe;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.Explosion;
//import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class RitualEffectUnspell extends RitualEffect {

	public RitualEffectUnspell(ResourceLocation ritualName) {
		super(ritualName);
	}

	@Override
    protected boolean applyRitualEffect(IRitualContext context) {
        ItemStack spellItem = ItemStack.EMPTY;
        CompoundNBT spellData = null;
        for (ItemStack stack : context.getCollectedReagents()) {
            if (stack.getTag() == null || !stack.getTag().contains("spell")) continue;
            spellItem = stack;
            break;
        }
        if (!spellItem.getTag().contains("ritual_reagent_data")) {
        	SpellRecipe recipe = SpellRecipe.fromNBT(spellItem.getOrCreateChildTag("spell"));
        	recipe.writeRecipeForRitual(context.getWorld(), spellItem.getOrCreateTag());
        }
        spellData = spellItem.getOrCreateChildTag("ritual_reagent_data");
        if (spellItem == null || spellData == null) {
            return false;
        }
        Affinity spellAffinity = ManaAndArtificeMod.getSpellHelper().parseSpellDefinition(spellItem).getHighestAffinity();
        NonNullList<ResourceLocation> reagentResources = NonNullList.create();
        reagentResources.addAll(getSpellReagentData(spellData, "shape_items"));
        reagentResources.addAll(getSpellReagentData(spellData, "component_items"));
        reagentResources.addAll(getSpellReagentData(spellData, "modifier_0_items"));
        reagentResources.addAll(getSpellReagentData(spellData, "modifier_1_items"));
        reagentResources.addAll(getSpellReagentData(spellData, "modifier_2_items"));
        reagentResources.addAll(getSpellReagentData(spellData, "complexity_reagent"));
            
        NonNullList<ItemStack> spellReagents = NonNullList.create();
        for (ResourceLocation indexItem : reagentResources)
        {
        	spellReagents.add( new ItemStack(ForgeRegistries.ITEMS.getValue(indexItem)));
        }

        context.getWorld().playSound(null, (double)context.getCenter().getX(), (double)context.getCenter().getY(), (double)context.getCenter().getZ(), 
        		SFX.Spell.Cast.ForAffinity(spellAffinity), SoundCategory.PLAYERS, 1.0f, 1.0f);
        context.getWorld().createExplosion((Entity)null, (float)context.getCenter().up().getX() + 0.5f, context.getCenter().up().getY(), 
        		(float)context.getCenter().up().getZ() + 0.5f, 1, false, Explosion.Mode.NONE);
        
        for (ItemStack spawnItem : spellReagents)
        {
            /*EntityPresentItem itemEntity = new EntityPresentItem(context.getWorld(), (float)context.getCenter().up().getX() + 0.5f, context.getCenter().up().getY(), 
            		(float)context.getCenter().up().getZ() + 0.5f, spawnItem);*/
        	ItemEntity itemEntity = new ItemEntity(context.getWorld(), (float)context.getCenter().up().getX() + 0.5f, context.getCenter().up().getY(), 
            		(float)context.getCenter().up().getZ() + 0.5f, spawnItem);
        	itemEntity.setNoDespawn();
        	itemEntity.setMotion(0.25 - (Math.random() * 0.5), Math.random() * 0.25, 0.25 - (Math.random() * 0.5));
        	context.getWorld().addEntity((Entity)itemEntity);
        }
        
        return true;
    }

    @Override
    protected int getApplicationTicks(IRitualContext context) {
        return 0;
    }

    @Override
    protected boolean modifyRitualReagentsAndPatterns(ItemStack dataStack, IRitualContext context) {
        if (dataStack.getItem() != ForgeRegistries.ITEMS.getValue(new ResourceLocation("mana-and-artifice:spell"))) {
            return false;
        }
        
        if (dataStack.getTag() != null && dataStack.getTag().contains("spell") && !dataStack.getTag().contains("ritual_reagent_data")) {
        	SpellRecipe recipe = SpellRecipe.fromNBT(dataStack.getChildTag("spell"));
        	recipe.writeRecipeForRitual(context.getWorld(), dataStack.getOrCreateTag());
        }
        
        context.replaceReagents(new ResourceLocation("arcanefundamentals:dynamic_spell"), reduntantlyGetSpellResourceLocationAsAList());
        context.replacePatterns(getInvertedWeavePatterns(dataStack));
        return true;
    }
    
    private NonNullList<ResourceLocation> reduntantlyGetSpellResourceLocationAsAList() {
		NonNullList<ResourceLocation> chosenItem = NonNullList.create();
		chosenItem.add(new ResourceLocation("mana-and-artifice:spell"));
		return chosenItem;
	}
    
    private NonNullList<ResourceLocation> getInvertedWeavePatterns(ItemStack spellItem) {
    	NonNullList<ResourceLocation> weavePatterns = NonNullList.create();
		weavePatterns.addAll(getSpellReagentData(spellItem.getOrCreateChildTag("ritual_reagent_data"), "pattern"));
    	Collections.reverse(weavePatterns);
		return weavePatterns;
    }
    
    private static NonNullList<ResourceLocation> getSpellReagentData(CompoundNBT nbt, String prefix) {
        NonNullList<ResourceLocation> resourceLocations = NonNullList.create();
        if (!nbt.contains(prefix + "_count")) {
            return resourceLocations;
        }
        int count = nbt.getInt(prefix + "_count");
        for (int i = 0; i < count; ++i) {
            String key = prefix + "_" + i;
            if (nbt.contains(key)) {
            	resourceLocations.add(new ResourceLocation(nbt.getString(key)));
                continue;
            }
        }
        return resourceLocations;
    }
    /*private void testreflect(World world, CompoundNBT spellNBT) throws ClassNotFoundException, NoSuchMethodException, SecurityException {
    	Class<?> spellRecipe = Class.forName("com.ma.spells.crafting.SpellRecipe");
    	
    	Method testMethod = spellRecipe.getDeclaredMethod("writeRecipeForRitual", World.class, CompoundNBT.class);
    }*/
}