package com.Spoilers.arcanefundamentals.entities;

import com.Spoilers.arcanefundamentals.ArcaneFundamentals;

//import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AFEntities {
	
	 public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, ArcaneFundamentals.MOD_ID);
	 
	 /*Entities*/
	 	//Utility
	 public static final RegistryObject<EntityType<?>> RITUAL_UTILITY_ENTITY = ENTITY_TYPES.register("ritual_utility", () -> EntityType.Builder.of(RitualUtilityEntity::new, (EntityClassification)EntityClassification.MISC).sized(0.5f, 0.5f).build("arcanefundamentals:ritual_utility"));
	 public static final RegistryObject<EntityType<?>> FACTION_RAID_ENTITY = ENTITY_TYPES.register("faction_raid_handler", () -> EntityType.Builder.of(FactionRaidHandlerEntity::new, (EntityClassification)EntityClassification.MISC).sized(1.0f, 1.0f).build("arcanefundamentals:faction_raid_handler"));
}
