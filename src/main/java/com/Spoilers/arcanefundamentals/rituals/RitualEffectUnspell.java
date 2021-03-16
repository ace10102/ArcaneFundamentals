package com.Spoilers.arcanefundamentals.rituals;

import java.util.Collections;

import com.Spoilers.arcanefundamentals.util.SpellContext;

import com.ma.api.affinity.Affinity;
import com.ma.api.rituals.IRitualContext;
import com.ma.api.rituals.RitualEffect;
import com.ma.api.sound.SFX;
/*import com.ma.entities.utility.EntityPresentItem;*/

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.Explosion;
import net.minecraftforge.registries.ForgeRegistries;

public class RitualEffectUnspell extends RitualEffect {
	
	private SpellContext spellContext;
	
	public RitualEffectUnspell(ResourceLocation ritualName) {
		super(ritualName);
	}

	@Override
    protected boolean applyRitualEffect(IRitualContext context) {
        ItemStack spellItem = ItemStack.EMPTY;
        for (ItemStack stack : context.getCollectedReagents()) {
            if (stack.getTag() == null || !stack.getTag().contains("spell")) continue;
            spellItem = stack;
            break;
        }
        if (spellItem == null || !spellContext.isValid) {
            return false;
        }
        
        Affinity spellAffinity = spellContext.getSpellAffinity();

        context.getWorld().playSound(null, (double)context.getCenter().getX(), (double)context.getCenter().getY(), (double)context.getCenter().getZ(), 
        		SFX.Spell.Cast.ForAffinity(spellAffinity), SoundCategory.PLAYERS, 1.0f, 1.0f);
        context.getWorld().createExplosion((Entity)null, (float)context.getCenter().up().getX() + 0.5f, context.getCenter().up().getY(), 
        		(float)context.getCenter().up().getZ() + 0.5f, 1, false, Explosion.Mode.NONE);
        
        for (ItemStack spawnItem : spellContext.fullSpellItems)
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
        spellContext = new SpellContext(dataStack, context.getWorld());
        if (!spellContext.isValid) {
        	return false;
        }
        context.replaceReagents(new ResourceLocation("arcanefundamentals:dynamic_spell"), reduntantlyGetSpellResourceLocationAsAList());
        context.replacePatterns(getInvertedWeavePatterns(spellContext.fullSpellPatterns));
        return true;
    }
    
    private NonNullList<ResourceLocation> reduntantlyGetSpellResourceLocationAsAList() {
		NonNullList<ResourceLocation> chosenItem = NonNullList.create();
		chosenItem.add(new ResourceLocation("mana-and-artifice:spell"));
		return chosenItem;
	}
    
    private NonNullList<ResourceLocation> getInvertedWeavePatterns(NonNullList<ResourceLocation> patterns) {
    	NonNullList<ResourceLocation> weavePatterns = NonNullList.create();
		weavePatterns.addAll(patterns);
    	Collections.reverse(weavePatterns);
		return weavePatterns;
    }
}