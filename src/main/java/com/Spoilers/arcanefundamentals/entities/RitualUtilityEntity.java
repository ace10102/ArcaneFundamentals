package com.Spoilers.arcanefundamentals.entities;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.Spoilers.arcanefundamentals.ArcaneFundamentals;
import com.Spoilers.arcanefundamentals.particle.AFParticleType;
import com.Spoilers.arcanefundamentals.particle.ParticleGetter;
import com.ma.api.ManaAndArtificeMod;
import com.ma.api.affinity.Affinity;
import com.ma.api.capabilities.Faction;
import com.ma.api.capabilities.IPlayerMagic;
import com.ma.api.sound.SFX;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

public class RitualUtilityEntity extends Entity {
    private static final DataParameter<String> RITUAL = EntityDataManager.defineId(RitualUtilityEntity.class, DataSerializers.STRING);
    private static final DataParameter<Byte> STATE = EntityDataManager.defineId(RitualUtilityEntity.class, DataSerializers.BYTE);
    private static final DataParameter<CompoundNBT> POSITIONS = EntityDataManager.defineId(RitualUtilityEntity.class, DataSerializers.COMPOUND_TAG);
    private NonNullList<BlockPos> positions = NonNullList.create();
    private int stateTicks = 0;
    private long position;
    private PlayerEntity player;
    private UUID playerUUID;
    private int playerTier;
    private static final DataParameter<Integer> FACTION = EntityDataManager.defineId(RitualUtilityEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> AFFINITY = EntityDataManager.defineId(RitualUtilityEntity.class, DataSerializers.INT);

    public RitualUtilityEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        this.setNoGravity(true);
        this.setSilent(false);
        this.setInvulnerable(true);
        this.noPhysics = true;
    }

    public RitualUtilityEntity(World world, String ritual, long position, PlayerEntity player, int tier) {
        super(AFEntities.RITUAL_UTILITY_ENTITY.get(), world);
        this.setRitual(ritual);
        this.position = position;
        this.setPlayer(player);
        this.playerTier = tier;
    }

    public void tick() {

        if (this.level.isClientSide) {
            switch (this.getRitual()) {
            
            case "arcanefundamentals:ritual-effect-catharsis": {
                Vector3d me = new Vector3d((this.getX()), (this.getY() + 1.25f), (this.getZ()));
                if (this.getState() == 0) {
                    this.spawnAffinityParticles(this.getAffinity());
                    if (stateTicks == 0) {
                        this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), this.getAffinityLoop(this.getAffinity()), SoundCategory.PLAYERS, (float)(0.75f + (0.25f * Math.random())), (float)(0.95f + (0.05f * Math.random())), false);
                        this.stateTicks = 1;
                    }
                    if (tickCount % 43 == 0)
                        this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), this.getAffinityLoop(this.getAffinity()), SoundCategory.PLAYERS, (float)(0.75f + (0.25f * Math.random())), (float)(0.95f + (0.05f * Math.random())), false);
                }
                else if (this.getState() == 1 && this.findNearbyPoints()) {
                    if (stateTicks == 1) {
                        this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SFX.Spell.Cast.ForAffinity(this.getAffinity().getOpposite()), SoundCategory.PLAYERS, 1.0f, 1.0f, false);
                        this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), this.getAffinityLoop(this.getAffinity().getOpposite()), SoundCategory.PLAYERS, (float)(0.75f + (0.25f * Math.random())), (float)(0.95f + (0.05f * Math.random())), false);
                    }
                    if (tickCount % 43 == 0)
                        this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), this.getAffinityLoop(this.getAffinity().getOpposite()), SoundCategory.PLAYERS, (float)(0.75f + (0.25f * Math.random())), (float)(0.95f + (0.05f * Math.random())), false);
                    
                    ParticleType<?> instance = AFParticleType.getCheckedInstance(ParticleGetter.getAffinityParticle(this.getAffinity().getOpposite()));
                    if (instance != null) { 
                         for (BlockPos pos : this.positions) {
                             Vector3d start = new Vector3d(pos.getX() + 0.5f, (pos.getY() + 0.5f), (pos.getZ() + 0.5f));
                             Random rnd = new Random();
                             for (int i = 0; i < 5; ++i) {
                                 this.level.addParticle((IParticleData)instance, start.x() - 0.15f + rnd.nextFloat() * 0.3f, start.y() - 0.15f + rnd.nextFloat() * 0.3f, start.z() - 0.15f + rnd.nextFloat() * 0.3f, me.x(), me.y()+(stateTicks/50d), me.z());
                             }
                         }
                    }
                    stateTicks++;
                }
            }
            }
        }

        if (!this.level.isClientSide) {
            switch (this.getRitual()) {

            case "arcanefundamentals:ritual-effect-treason": {
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

            case "arcanefundamentals:ritual-effect-catharsis": {
                if (this.getPlayer() == null) {
                    ArcaneFundamentals.LOGGER.error("Failed to load player reference-killing ritual utility entity");
                    this.remove();//temporary measure due to not easily reproducable crashloop
                }
                if (tickCount % 20 == 0) {
                    List<Entity> centerCollisions = this.level.getEntities(this, this.getBoundingBox(), null);
                    IPlayerMagic playerMagic = getPlayer().getCapability(ManaAndArtificeMod.getMagicCapability()).orElse(null);
                    
                    if (centerCollisions.contains(getPlayer()) && this.getState() == 0) {
                        this.findNearbyPoints();
                        if (playerMagic == null || !playerMagic.isMagicUnlocked())
                            this.remove();
                        this.setState((byte)1);
                    }
                    
                    if (this.getState() == 1) { 
                        if (!this.getPlayer().hasEffect(ForgeRegistries.POTIONS.getValue(new ResourceLocation("mana-and-artifice:lift")))) {
                            this.getPlayer().addEffect(new EffectInstance(ForgeRegistries.POTIONS.getValue(new ResourceLocation("mana-and-artifice:lift")), 90, 1));
                        }
                        if (this.stateTicks >= 4) {
                            List<Entity> affinityEntities = this.getNearbyAffinityEntities();
                            for (Entity e : affinityEntities) {
                                e.remove();
                            }
                            this.getPlayer().curePotionEffects(new ItemStack(Items.MILK_BUCKET));
                            for (Map.Entry<Affinity, Float> e : playerMagic.getSortedAffinityDepths().entrySet()) {
                                for(int i = (int)(e.getValue()/25); i >= 1; i--) {
                                    ItemStack mote = new ItemStack(this.getMoteForAffinity(e.getKey()));
                                    ItemEntity itemEntity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), mote);//make present item
                                    itemEntity.setExtendedLifetime();
                                    itemEntity.setDeltaMovement(0.25 - (Math.random() * 0.5), Math.random() * 0.25, 0.25 - (Math.random() * 0.5));
                                    this.level.addFreshEntity(itemEntity);
                                }
                                playerMagic.setAffinityDepth(e.getKey(), 0f);
                            }
                            playerMagic.forceSync();
                            this.remove();
                        }
                        this.stateTicks++;
                    }
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
    
//    private ParticleType<?> getAffinityParticle(Affinity affinity) {
//        switch (affinity) {
//        case ARCANE: return ParticleInit.ARCANE_LERP.get(); 
//        case EARTH: return ParticleInit.DUST_LERP.get(); 
//        case ENDER: return ParticleInit.ENDER.get(); 
//        case FIRE: return ParticleInit.FLAME_LERP.get(); 
//        case LIGHTNING: return ParticleInit.LIGHTNING_BOLT.get(); 
//        case WATER: return ParticleInit.WATER_LERP.get(); 
//        case ICE: return ParticleInit.FROST_LERP.get(); 
//        case WIND: return ParticleInit.AIR_LERP.get(); 
//        default: return ParticleInit.SPARKLE_LERP_POINT.get();
//        }
//    }
    
    private SoundEvent getAffinityLoop(Affinity affinity) {
        switch (affinity) {
        case ARCANE: return SFX.Loops.ARCANE;
        case EARTH: return SFX.Loops.EARTH;
        case ENDER: return SFX.Loops.ENDER;
        case FIRE: return SFX.Loops.FIRE;
        case LIGHTNING: return SFX.Loops.LIGHTNING;
        case WATER: return SFX.Loops.WATER;
        case ICE: return SFX.Loops.ICE;
        case WIND: return SFX.Loops.AIR;
        default: return SFX.Loops.MANAWEAVING;
        }
    }
    
    private Item getMoteForAffinity(Affinity affinity) {
        switch (affinity.getShiftAffinity()) {
        case ARCANE: return ForgeRegistries.ITEMS.getValue(new ResourceLocation("mana-and-artifice:mote_arcane"));
        case EARTH: return ForgeRegistries.ITEMS.getValue(new ResourceLocation("mana-and-artifice:mote_earth"));
        case ENDER: return ForgeRegistries.ITEMS.getValue(new ResourceLocation("mana-and-artifice:mote_ender"));
        case FIRE: return ForgeRegistries.ITEMS.getValue(new ResourceLocation("mana-and-artifice:mote_fire"));
        case WATER: return ForgeRegistries.ITEMS.getValue(new ResourceLocation("mana-and-artifice:mote_water"));
        case WIND: return ForgeRegistries.ITEMS.getValue(new ResourceLocation("mana-and-artifice:mote_air"));
        default: return ForgeRegistries.ITEMS.getValue(new ResourceLocation("mana-and-artifice:cerublossom"));
        }
    }

    private void spawnAffinityParticles(Affinity affinity) {
        ParticleType<?> instance = AFParticleType.getCheckedInstance(ParticleGetter.getAffinityParticle(affinity));
        if (instance != null) { 
             int i;
             for (i = 0; i < 15; ++i) {   
                 this.level.addParticle((IParticleData)instance, this.getX() - 0.5 + (Math.random() * 1f), this.getY(), this.getZ() - 0.5 + (Math.random() * 1f), this.getX() - 0.25f + (Math.random() * 0.5f), this.getY() + 2.5f - (Math.random() * 0.5f), this.getZ() - 0.25f + (Math.random() * 0.5f));
             }
             for (i = 0; i < 10; ++i) {
                 this.level.addParticle((IParticleData)instance, this.getX() - 2f + (Math.random() * 4.0f), this.getY()-0.05f, this.getZ() - 2f + (Math.random() * 4.0f), this.getX(), this.getY() + 1.25f, this.getZ());
             }
        }
    }
    
    private NonNullList<Entity> getNearbyAffinityEntities() {
        int search_radius = 3;
        AxisAlignedBB bb = new AxisAlignedBB(this.blockPosition()).inflate(search_radius);
        NonNullList<Entity> entities = NonNullList.create();
        for (Entity e : this.level.getEntities(null, bb)) {
            if (e.getType().getRegistryName().toString().equals("mana-and-artifice:affinity_icon_entity")) {
                entities.add(e);
            }
        }
        return entities;
    }
    
    private boolean findNearbyPoints() {
        List<Entity> entities = this.getNearbyAffinityEntities();
        if (entities.size() < 4) {
            return false;
        }
        this.positions.clear();
        entities.stream().forEach(e -> this.positions.add(new BlockPos(e.position())));
        return true;
    }
    
    public Affinity getEntityAffinity(Entity entity) {
        CompoundNBT nbt = entity.serializeNBT();
        if (nbt.contains("affinity")) {        
            return Affinity.valueOf(nbt.getString("affinity"));
        }
        else {
            if(!this.level.isClientSide) {
                ArcaneFundamentals.LOGGER.error("Failed to load affinity for Affinity Icon Entity");
            }
            return Affinity.UNKNOWN;
        } 
    }
    
    private void setPlayer(PlayerEntity player) {
        this.player = player;
        this.playerUUID = player.getUUID();
    }
    
    private PlayerEntity getPlayer() {
        if (this.player == null && this.playerUUID != null) {
            this.player = this.level.getPlayerByUUID(this.playerUUID);
        }
        return this.player;
    }

    private void writeKnownPositions(CompoundNBT nbt) {
        CompoundNBT known = new CompoundNBT();
        known.putInt("count", this.positions.size());
        for (int i = 0; i < this.positions.size(); ++i) {
            known.put("pos" + i, NBTUtil.writeBlockPos(this.positions.get(i)));
        }
        nbt.put("known_pos", known);
    }

    private void readKnownPositions(CompoundNBT nbt) {
        if (nbt.contains("known_pos")) {
            CompoundNBT known = nbt.getCompound("known_pos");
            int count = known.getInt("count");
            this.positions.clear();
            for (int i = 0; i < count; ++i) {
                try {
                    BlockPos pos = NBTUtil.readBlockPos(known.getCompound("pos" + i));
                    this.positions.add(pos);
                    continue;
                } catch (Exception exception) {
                    // empty catch block
                }
            }
        }
    }

    protected void defineSynchedData() {
        this.entityData.define(RITUAL, " ");
        this.entityData.define(STATE, (byte)0);
        this.entityData.define(FACTION, 0);
        this.entityData.define(AFFINITY, 0);
        this.entityData.define(POSITIONS, new CompoundNBT());
    }

    protected void readAdditionalSaveData(CompoundNBT compound) {
        if (compound.contains("ritual")) this.entityData.set(RITUAL, compound.getString("ritual"));
        if (compound.contains("state")) this.entityData.set(STATE, compound.getByte("state"));
        this.readKnownPositions(compound);
        if (compound.contains("position")) this.position = compound.getLong("position");
        if (compound.contains("player")) {
            try {
                this.playerUUID = UUID.fromString(compound.getString("player"));
            } catch (Exception ex) {
                ArcaneFundamentals.LOGGER.error("Failed to load player UUID when loading ritual handler.  Skipping and despawning the handler.");
            }
        }
        if (compound.contains("playerTier")) this.playerTier = compound.getInt("playerTier"); 
        if (compound.contains("faction")) this.entityData.set(FACTION, compound.getInt("faction"));
        if (compound.contains("affinity")) this.entityData.set(AFFINITY, compound.getInt("affinity"));   
    }

    protected void addAdditionalSaveData(CompoundNBT compound) {
        compound.putString("ritual", this.entityData.get(RITUAL));
        compound.putByte("state", this.entityData.get(STATE));
        this.writeKnownPositions(compound);
        compound.putLong("position", this.position);
        if (this.player != null) {
            compound.putString("player", this.player.getUUID().toString());
        }
        compound.putInt("playerTier", this.playerTier);
        compound.putInt("faction", (this.entityData.get(FACTION)).intValue());
        compound.putInt("affinity", (this.entityData.get(AFFINITY)).intValue());
    }

    public void setRitual(String ritual) {
        this.entityData.set(RITUAL, ritual);
    }
    public String getRitual() {
        return this.entityData.get(RITUAL);
    }
    
    protected void setState(byte state) {
        this.entityData.set(STATE, state);
        this.stateTicks = 0;
    }
    public byte getState() {
        return this.entityData.get(STATE);
    }

    public void setFaction(Faction faction) {
        this.entityData.set(FACTION, faction.ordinal());
    }
    public Faction getFaction() {
        return Faction.values()[this.entityData.get(FACTION)];
    }

    public void setAffinity(Affinity affinity) {
        this.entityData.set(AFFINITY, affinity.ordinal());
    }
    public Affinity getAffinity() {
        return Affinity.values()[this.entityData.get(AFFINITY)];
    }

    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}