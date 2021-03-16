package com.Spoilers.arcanefundamentals.util;

import java.util.Arrays;
import java.util.List;

import com.ma.api.ManaAndArtificeMod;
import com.ma.api.affinity.Affinity;
import com.ma.api.recipes.IItemAndPatternRecipe;
import com.ma.api.spells.base.IModifiedSpellPart;
import com.ma.api.spells.base.ISpellComponent;
import com.ma.api.spells.base.ISpellDefinition;
import com.ma.api.spells.parts.Component;
import com.ma.api.spells.parts.Modifier;
import com.ma.api.spells.parts.Shape;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class SpellContext {
	public final IRecipeType shapeRecipe = Registry.RECIPE_TYPE.getOrDefault(new ResourceLocation("mana-and-artifice", "shape-recipe-type"));
	public final IRecipeType componentRecipe = Registry.RECIPE_TYPE.getOrDefault(new ResourceLocation("mana-and-artifice", "component-recipe-type"));
	public final IRecipeType modifierRecipe = Registry.RECIPE_TYPE.getOrDefault(new ResourceLocation("mana-and-artifice", "modifier-recipe-type"));
	private static final ResourceLocation Minor_Rloc = (new ResourceLocation("mana-and-artifice","ritual_focus_minor"));
    private static final ResourceLocation Lesser_Rloc = (new ResourceLocation("mana-and-artifice","ritual_focus_lesser"));
    private static final ResourceLocation Greater_Rloc = (new ResourceLocation("mana-and-artifice","ritual_focus_greater"));
	private static final int Minor_Complexity = 10;
    private static final int Lesser_Complexity = 20;
    private static final int Greater_Complexity = 50;
	
	public ISpellDefinition spellData;
	public boolean isValid = false;
	
	public Affinity spellAffinity;
	
	public Shape spellShape;
	public ResourceLocation shapeRloc;
	public List<ResourceLocation> shapeItems;
	public List<ResourceLocation> shapePatterns;
	
	public Component spellComponent;
	public ResourceLocation componentRloc;
	public List<Component> spellComponents;
	public List<ResourceLocation> componentsRlocs;
	public List<ResourceLocation> componentsItems;
	public List<ResourceLocation> componentsPatterns;
	
	public List<Modifier> spellModifiers;
	public List<ResourceLocation> modifiersRlocs;
	public List<ResourceLocation> modifiersItems;
	public List<ResourceLocation> modifiersPatterns;
	
	public float spellComplexity;
	public List<ResourceLocation> complexityItems;
	
	public NonNullList<ResourceLocation> fullSpellRlocs;
	public NonNullList<ItemStack> fullSpellItems;
	public NonNullList<ResourceLocation> fullSpellPatterns;
	
	public SpellContext(ItemStack spellItem, World world) {
		this.spellData = ManaAndArtificeMod.getSpellHelper().parseSpellDefinition(spellItem);
		this.isValid = spellData.isValid();
		if (isValid) {
			this.spellAffinity = spellData.getHighestAffinity();
			
			this.spellShape = spellData.getShape().getPart();
			this.shapeRloc = spellShape.getRegistryName();
			this.shapeItems = getRecipeItems(world, shapeRloc, shapeRecipe);
			this.shapePatterns = getRecipePatterns(world, shapeRloc, shapeRecipe);
			
			this.spellComponent = spellData.getComponent(0).getPart();
			this.componentRloc = spellComponent.getRegistryName();
			this.spellComponents = getTrueComponents();
			this.componentsRlocs = getPartsRlocs(spellComponents);
			this.componentsItems = getRecipesItems(world, componentsRlocs, componentRecipe);
			this.componentsPatterns = getRecipesPatterns(world, componentsRlocs, componentRecipe);
			
			this.spellModifiers = spellData.getModifiers();
			this.modifiersRlocs = getPartsRlocs(spellModifiers);
			this.modifiersItems = getRecipesItems(world, modifiersRlocs, modifierRecipe);
			this.modifiersPatterns = getRecipesPatterns(world, modifiersRlocs, modifierRecipe);
			
			this.spellComplexity = spellData.getComplexity();
			this.complexityItems = getComplexityItems();
			
			this.fullSpellRlocs = getSpellRlocs();
			this.fullSpellItems = getAllSpellRecipeItems();
			this.fullSpellPatterns = getAllSpellRecipePatterns();
		}
	}
	
	public Affinity getSpellAffinity() {
		return spellAffinity;
	}
	
	public List<Component> getTrueComponents() {
		NonNullList<Component> components = NonNullList.create();
		for (IModifiedSpellPart<Component> part : spellData.getComponents()) {
			components.add(part.getPart());
		}
		return components;
	}
	
	public <T extends ISpellComponent> List<ResourceLocation> getPartsRlocs(List<T> partsList) {
		NonNullList<ResourceLocation> rlocs = NonNullList.create();
		for(T partIterate : partsList) {
			rlocs.add(partIterate.getRegistryName());
		}
		return rlocs;
	}
	
	public NonNullList<ResourceLocation> getSpellRlocs() {
		NonNullList<ResourceLocation> spellRlocs = NonNullList.create();
		
		spellRlocs.add(shapeRloc);
		spellRlocs.addAll(componentsRlocs);
		spellRlocs.addAll(modifiersRlocs);
		
		return spellRlocs;
	}
	
	public <T extends SpecialRecipe> List<ResourceLocation> getRecipeItems(World world, ResourceLocation spellPart, IRecipeType<T> type) {
		NonNullList<ResourceLocation> itemRlocsForPart = NonNullList.create();
		List<T> recipes = world.getRecipeManager().getRecipesForType(type);
		for (T recipeTest : recipes) {
			if (recipeTest.getId().equals(spellPart) && recipeTest instanceof IItemAndPatternRecipe) {
				itemRlocsForPart.addAll(Arrays.asList(((IItemAndPatternRecipe)recipeTest).getRequiredItems()));
			}
		}
		//System.out.println(itemRlocsForPart);
		return itemRlocsForPart;
	}
	
	public <T extends SpecialRecipe> List<ResourceLocation> getRecipePatterns(World world, ResourceLocation spellPart, IRecipeType<T> type) {
		NonNullList<ResourceLocation> patternRlocsForPart = NonNullList.create();
		List<T> recipes = world.getRecipeManager().getRecipesForType(type);
		for (T recipeTest : recipes) {
			if (recipeTest.getId().equals(spellPart) && recipeTest instanceof IItemAndPatternRecipe) {
				patternRlocsForPart.addAll(Arrays.asList(((IItemAndPatternRecipe)recipeTest).getRequiredPatterns()));
			}
		}
		//System.out.println(patternRlocsForPart);
		return patternRlocsForPart;
	}
	
	public <T extends SpecialRecipe> List<ResourceLocation> getRecipesItems(World world, List<ResourceLocation> spellParts, IRecipeType<T> type) {
		NonNullList<ResourceLocation> itemsForParts = NonNullList.create();
		List<T> recipes = world.getRecipeManager().getRecipesForType(type);
		for (ResourceLocation spellIterate : spellParts) {
			for (T recipeTest : recipes) {
				if (recipeTest.getId().equals(spellIterate) && recipeTest instanceof IItemAndPatternRecipe) {
					itemsForParts.addAll(Arrays.asList(((IItemAndPatternRecipe)recipeTest).getRequiredItems()));
				}
			}
		}
		//System.out.println(itemsForParts);
		return itemsForParts;
	}
	
	public <T extends SpecialRecipe> List<ResourceLocation> getRecipesPatterns(World world, List<ResourceLocation> spellParts, IRecipeType<T> type) {
		NonNullList<ResourceLocation> patternsForParts = NonNullList.create();
		List<T> recipes = world.getRecipeManager().getRecipesForType(type);
		for (ResourceLocation spellIterate : spellParts) {
			for (T recipeTest : recipes) {
				if (recipeTest.getId().equals(spellIterate) && recipeTest instanceof IItemAndPatternRecipe) {
					patternsForParts.addAll(Arrays.asList(((IItemAndPatternRecipe)recipeTest).getRequiredPatterns()));
				}
			}
		}
		//System.out.println(patternsForParts);
		return patternsForParts;
	}
	
	public List<ResourceLocation> getComplexityItems() {
		NonNullList<ResourceLocation> complexityItems = NonNullList.create();
		int cieling = (int)Math.ceil(spellComplexity);
        for (int i = 0; i < 4 && (cieling -= addLargestComplexityItem((NonNullList<ResourceLocation>)complexityItems, cieling, i == 3)) > 0; ++i) {
        }
		return complexityItems;
	}
	
	private int addLargestComplexityItem(NonNullList<ResourceLocation> list, int complexity, boolean mustContain) {
        if (mustContain) {
            if (complexity <= Lesser_Complexity) {
                list.add(Lesser_Rloc);
                return Lesser_Complexity;
            }
            list.add(Greater_Rloc);
            return Greater_Complexity;
        }
        if (complexity <= Minor_Complexity * 3) {
            list.add(Minor_Rloc);
            return Minor_Complexity;
        }
        if (complexity < Lesser_Complexity * 3) {
            list.add(Lesser_Rloc);
            return Lesser_Complexity;
        }
        list.add(Greater_Rloc);
        return Greater_Complexity;
    }
	
	public NonNullList<ItemStack> getAllSpellRecipeItems() {
		NonNullList<ItemStack> spellItems = NonNullList.create();
		NonNullList<ResourceLocation> spellItemRlocs = NonNullList.create();
		spellItemRlocs.addAll(shapeItems);
		spellItemRlocs.addAll(componentsItems);
		spellItemRlocs.addAll(modifiersItems);
		//spellItemRlocs.addAll(complexityItems);
	    for (ResourceLocation indexRloc : spellItemRlocs)
	    {
	    	spellItems.add( new ItemStack(ForgeRegistries.ITEMS.getValue(indexRloc)));
	    }
		return spellItems;
 	}
	
	public NonNullList<ResourceLocation> getAllSpellRecipePatterns() {
		NonNullList<ResourceLocation> spellPatterns = NonNullList.create();
		spellPatterns.addAll(shapePatterns);
		spellPatterns.addAll(componentsPatterns);
		spellPatterns.addAll(modifiersPatterns);
		return spellPatterns;
	}
	
	@Override
	public String toString() {
		return (isValid + "\n" + spellAffinity + "\n" + fullSpellRlocs + "\n" + spellComplexity + "\n" + fullSpellItems + "\n" + fullSpellPatterns);
	}
}
