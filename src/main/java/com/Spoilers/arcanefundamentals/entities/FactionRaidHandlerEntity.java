package com.Spoilers.arcanefundamentals.entities;

import java.util.UUID;

import com.Spoilers.arcanefundamentals.ArcaneFundamentals;
import com.ma.api.capabilities.Faction;
import com.ma.api.entities.FactionRaidRegistry;
import com.ma.api.entities.IFactionEnemy;
import com.ma.api.particles.ParticleInit;
import com.ma.api.sound.SFX;
import com.mojang.datafixers.util.Pair;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class FactionRaidHandlerEntity extends Entity {
    private static final DataParameter<Integer> FACTION = EntityDataManager.defineId(FactionRaidHandlerEntity.class, DataSerializers.INT);
    private PlayerEntity player;
    private UUID playerUUID;
    private int strength;

    public FactionRaidHandlerEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    public FactionRaidHandlerEntity(World world, PlayerEntity target, int strength) {
        super(AFEntities.FACTION_RAID_ENTITY.get(), world);
        this.player = target;
        this.strength = strength;
    }

    public void tick() {
        if (!this.level.isClientSide && this.tickCount % 20 == 0 && !this.spawnRaidEntity()) {
            this.remove(false);
        }
        if (this.level.isClientSide) {
            if (this.getFaction() == Faction.DEMONS) {
                this.spawnDemonParticles();
            } else if (this.getFaction() == Faction.UNDEAD) {
                this.spawnUndeadParticles();
            }
        } else if (this.tickCount == 20) {
            switch (this.getFaction()) {
                case ANCIENT_WIZARDS: {
                    this.playSound(SFX.Event.Faction.FACTION_RAID_COUNCIL, 1.0f, 1.0f);
                    break;
                }
                case DEMONS: {
                    this.playSound(SFX.Event.Faction.FACTION_RAID_DEMONS, 1.0f, 1.0f);
                    break;
                }
                case FEY_COURT: {
                    this.playSound(SFX.Event.Faction.FACTION_RAID_FEY, 1.0f, 1.0f);
                    break;
                }
                case UNDEAD: {
                    this.playSound(SFX.Event.Faction.FACTION_RAID_DEMONS, 1.0f, 1.0f);
                    break;
                }
			default:
				break;
            }
        }
    }

    private void spawnDemonParticles() {
        int i;
        for (i = 0; i < 15; ++i) {
            this.level.addParticle((IParticleData)ParticleInit.HELLFIRE.get(), this.getX(), this.getY(), this.getZ(), -0.05f + Math.random() * 0.1f, 0.1f, -0.05f + Math.random() * 0.1f);
        }
        this.level.addParticle((IParticleData)ParticleTypes.LAVA, this.getX() - 0.5 + Math.random() * 1.0, this.getY(), this.getZ() - 0.5 + Math.random() * 1.0, 0.0, 0.05f, 0.0);
        for (i = 0; i < 5; ++i) {
            this.level.addParticle((IParticleData)ParticleTypes.LANDING_LAVA, this.getX() - 1.5 + Math.random() * 3.0, this.getY(), this.getZ() - 1.5 + Math.random() * 3.0, 0.0, 0.05f, 0.0);
        }
    }

    private void spawnUndeadParticles() {
        int i;
        for (i = 0; i < 15; ++i) {
            this.level.addParticle(ParticleInit.FROST.get(), this.getX(), this.getY(), this.getZ(), -0.05f + Math.random() * 0.1f, 0.1f, -0.05f + Math.random() * 0.1f);
        }
        for (i = 0; i < 5; ++i) {
            this.level.addParticle(ParticleInit.BONE.get(), this.getX() - 1.5 + Math.random() * 3.0, this.getY(), this.getZ() - 1.5 + Math.random() * 3.0, 0.0, 0.05f, 0.0);
        }
    }

    private PlayerEntity getPlayer() {
        if (this.player == null && this.playerUUID != null) {
            this.player = this.level.getPlayerByUUID(this.playerUUID);
        }
        return this.player;
    }

    private boolean spawnRaidEntity() {
        if (getPlayer() == null) {
            return false;
        }
        Pair<EntityType<? extends IFactionEnemy<? extends MobEntity>>, Integer> soldier = FactionRaidRegistry.getSoldier(this.getFaction(), this.strength);
        if (soldier == null) {
            return false;
        }
        IFactionEnemy<? extends MobEntity> entity = (IFactionEnemy<? extends MobEntity>)(soldier.getFirst()).create(this.level);
        entity.setRaidTarget(getPlayer());
        entity.setTier(soldier.getSecond());
        ((LivingEntity)entity).setPos(this.getX()-1+Math.random(), this.getY(), this.getZ()-1+Math.random());
        
        switch(this.getFaction()) {
        case ANCIENT_WIZARDS: ((LivingEntity)entity).addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 600, 1)); 
        	break;
        case DEMONS: ((LivingEntity)entity).addEffect(new EffectInstance(Effects.DAMAGE_BOOST, 300, 1));
        	break;
        case FEY_COURT: ((LivingEntity)entity).addEffect(new EffectInstance(Effects.REGENERATION, 300, 1));
        	break;
        case UNDEAD: ((LivingEntity)entity).addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 600, 1));
        	break;
		default:
			break;
        }
        
        this.level.addFreshEntity((Entity)entity);
        int soldier_strength = FactionRaidRegistry.getStrengthRating(this.getFaction(), (EntityType<? extends IFactionEnemy<? extends MobEntity>>)(soldier.getFirst()), soldier.getSecond());
        if (soldier_strength == -1) {
            return false;
        }
        this.strength -= soldier_strength;
        return this.strength > 0;
    }

    protected void defineSynchedData() {
        this.entityData.define(FACTION, 0);
    }

    protected void readAdditionalSaveData(CompoundNBT compound) {
        if (compound.contains("strength")) {
            this.strength = compound.getInt("strength");
        }
        if (compound.contains("faction")) {
            this.entityData.set(FACTION, compound.getInt("faction"));
        }
        if (compound.contains("target")) {
            try {
                this.playerUUID = UUID.fromString(compound.getString("target"));
            }
            catch (Exception ex) {
                ArcaneFundamentals.LOGGER.error("Failed to load player UUID when loading faction raid.  Skipping and despawning the raid.");
            }
        }
    }

    protected void addAdditionalSaveData(CompoundNBT compound) {
        compound.putInt("strength", this.strength);
        compound.putInt("faction", (this.entityData.get(FACTION)).intValue());
        if (this.player != null) {
            compound.putString("target", this.player.getUUID().toString());
        }
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