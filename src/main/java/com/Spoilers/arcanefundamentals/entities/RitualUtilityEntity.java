package com.Spoilers.arcanefundamentals.entities;

import java.util.UUID;

import com.Spoilers.arcanefundamentals.ArcaneFundamentals;
import com.ma.api.capabilities.Faction;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class RitualUtilityEntity extends Entity {
    private String ritual;
    private long position;
    private PlayerEntity player;
    private UUID playerUUID;
    private int playerTier;
    private static final DataParameter<Integer> FACTION = EntityDataManager.defineId(RitualUtilityEntity.class, DataSerializers.INT);

    public RitualUtilityEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    public RitualUtilityEntity(World world, String ritual, long position, PlayerEntity player, int tier) {
        super(AFEntities.RITUAL_UTILITY_ENTITY.get(), world);
        this.ritual = ritual;
        this.position = position;
        this.player = player;
        this.playerTier = tier;
    }

    public void tick() {

    	if(this.level.isClientSide) {
    		
    	}
    	
    	if(!this.level.isClientSide) {
    		switch (this.ritual) {
    	
    			case "arcanefundamentals:ritual-effect-treachery": {
    				if (tickCount % 80 == 0 || (playerTier - 1) < 1) {
    					this.remove();
    				}
    				if (tickCount % 20 == 0) {        	
    					
    					BlockPos randomPos = BlockPos.of(position).offset(-8.5 + (Math.random() * 17), 0, -8.5 + (Math.random() * 17));
    					int randStrength = (30 * playerTier) + (int)(-20 + (Math.random() * 40));
    					spawnRaidHandler(this.level, getPlayer(), randomPos, getFaction(), randStrength);
    					playerTier--;
    				}
    			}
    		}
    	}
    }
    
    private void spawnRaidHandler(World world, PlayerEntity player, BlockPos pos, Faction faction, int strength) {
		FactionRaidHandlerEntity raidHandler = new FactionRaidHandlerEntity(world, player, strength);
		raidHandler.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		raidHandler.setFaction(faction);
		world.addFreshEntity(raidHandler);
	}

    private PlayerEntity getPlayer() {
        if (this.player == null && this.playerUUID != null) {
            this.player = this.level.getPlayerByUUID(this.playerUUID);
        }
        return this.player;
    }

    protected void defineSynchedData() {
        this.entityData.define(FACTION, 0);
    }

    protected void readAdditionalSaveData(CompoundNBT compound) {
    	if (compound.contains("ritual")) {
    		this.ritual = compound.getString("ritual");
    	}
    	if (compound.contains("position")) {
    		this.position = compound.getLong("position");
    	}
    	if (compound.contains("player")) {
    		try {
    			this.playerUUID = UUID.fromString(compound.getString("player"));
    		}
    		catch (Exception ex) {
    			ArcaneFundamentals.LOGGER.error("Failed to load player UUID when loading ritual handler.  Skipping and despawning the handler.");
    		}
    	}
    	if (compound.contains("playerTier")) {
    		this.playerTier = compound.getInt("playerTier");
    	}
    	if (compound.contains("faction")) {
    		this.entityData.set(FACTION, compound.getInt("faction"));
    	}
    }

    protected void addAdditionalSaveData(CompoundNBT compound) {
    	compound.putString("ritual", this.ritual);
    	compound.putLong("position", this.position);
    	if (this.player != null) {
    		compound.putString("player", this.player.getUUID().toString());
    	}
    	compound.putInt("playerTier", this.playerTier);
    	compound.putInt("faction", (this.entityData.get(FACTION)).intValue());      
    }

    public Faction getFaction() {
        return Faction.values()[this.entityData.get(FACTION)];
    }

    public void setFaction(Faction faction) {
        this.entityData.set(FACTION, faction.ordinal());
    }

    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}