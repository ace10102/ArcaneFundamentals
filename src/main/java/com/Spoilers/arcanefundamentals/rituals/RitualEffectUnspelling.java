package com.Spoilers.arcanefundamentals.rituals;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.Spoilers.arcanefundamentals.ArcaneFundamentals;
import com.Spoilers.arcanefundamentals.particle.AFParticleType;
import com.Spoilers.arcanefundamentals.particle.ParticleGetter;
import com.Spoilers.arcanefundamentals.util.SpellContext;
import com.ma.api.affinity.Affinity;
import com.ma.api.rituals.IRitualContext;
import com.ma.api.rituals.RitualEffect;
import com.ma.api.sound.SFX;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class RitualEffectUnspelling extends RitualEffect {

    private SpellContext spellContext;
    private NonNullList<BlockPos> positions = NonNullList.create();
    private int tickCounter = 0;

    public RitualEffectUnspelling(ResourceLocation ritualName) {
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
        if (spellItem == null || spellContext == null || !spellContext.isValid) {
            return false;
        }
        
        World world = context.getWorld();
        BlockPos center = context.getCenter();
        
        Affinity spellAffinity = spellContext.getSpellAffinity();

        world.playSound(null, center.getX(), center.getY(), center.getZ(), SFX.Spell.Cast.ForAffinity(spellAffinity), SoundCategory.PLAYERS, 1.5f, 1.0f);
        world.explode(null, center.above().getX() + 0.5f, center.above().getY(), center.above().getZ() + 0.5f, 1, false, Explosion.Mode.NONE);

        for (ItemStack spawnItem : spellContext.fullSpellItems) {
            ItemEntity itemEntity = new ItemEntity(world, center.above().getX() + 0.5f, center.above().getY(), center.above().getZ() + 0.5f, spawnItem);
            itemEntity.setExtendedLifetime();
            itemEntity.setDeltaMovement(0.25 - (Math.random() * 0.5), Math.random() * 0.25, 0.25 - (Math.random() * 0.5));
            world.addFreshEntity(itemEntity);
        }
        
        for (Entity e : getNearbyAffinityEntities(context)) {
            e.remove();
        }
        
        BlockPos[] reservoirPos = new BlockPos[] {center.offset(3, 0, 0), center.offset(-3, 0, 0), center.offset(0, 0, -3), center.offset(0, 0, 3)};
        Block reservoir = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("mana-and-artifice:mana_resevoir"));
        for (BlockPos pos : reservoirPos) {
            if(!world.getBlockState(pos).getBlock().is(reservoir) || world.getBlockState(pos).getValue(IntegerProperty.create("fill_level", 0, 4)) != 4) {
                continue;
            } else {
                CompoundNBT updateMana = world.getBlockEntity(pos).serializeNBT();
                updateMana.putFloat("mana", 0f);
                world.getBlockEntity(pos).deserializeNBT(updateMana);
                BlockState test = world.getBlockState(pos).setValue(IntegerProperty.create("fill_level", 0, 4), 0);
                world.setBlockAndUpdate(pos, test);
            }
        }
        
        spellContext = null;
        positions.clear();
        tickCounter = 0;
        return true;
    }

    @Override
    protected int getApplicationTicks(IRitualContext context) {
        return 0;
    }

    @Override
    protected boolean modifyRitualReagentsAndPatterns(ItemStack dataStack, IRitualContext context) {
        if (dataStack.getItem() != ForgeRegistries.ITEMS.getValue(new ResourceLocation("mana-and-artifice:spell"))) {
            spellContext = null;
            return false;
        }
        spellContext = new SpellContext(dataStack, context.getWorld());
        if (!spellContext.isValid) {
            spellContext = null;
            return false;
        }//modify to handle wands/staves?
        context.replacePatterns(getInvertedWeavePatterns(spellContext.fullSpellPatterns));
        return true;
    }

    private NonNullList<ResourceLocation> getInvertedWeavePatterns(NonNullList<ResourceLocation> patterns) {
        NonNullList<ResourceLocation> weavePatterns = NonNullList.create();
        weavePatterns.addAll(patterns);
        Collections.reverse(weavePatterns);
        return weavePatterns;
    }
    
    @Override
    public ITextComponent canRitualStart(IRitualContext context) {
        if (spellContext == null) 
            return null;
        BlockPos[] reservoirPos = new BlockPos[] {context.getCenter().offset(3, 0, 0), context.getCenter().offset(-3, 0, 0), context.getCenter().offset(0, 0, -3), context.getCenter().offset(0, 0, 3)};
        Block reservoir = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("mana-and-artifice:mana_resevoir"));
        for (BlockPos pos : reservoirPos) {
            if(!context.getWorld().getBlockState(pos).getBlock().is(reservoir)) {
                return new TranslationTextComponent("ritual.arcanefundamentals.no_reservoir", pos.toShortString());
            } else if ( context.getWorld().getBlockEntity(pos).serializeNBT().getFloat("mana") != 100f) {
                return new TranslationTextComponent("ritual.arcanefundamentals.no_mana", pos.toShortString());
            }
        }
        BlockPos[] crystalPos = new BlockPos[] {context.getCenter().offset(2, 0, 2), context.getCenter().offset(2, 0, -2), context.getCenter().offset(-2, 0, 2), context.getCenter().offset(-2, 0, -2)};
        ITag<Block> afTags = BlockTags.getAllTags().getTagOrEmpty(new ResourceLocation("arcanefundamentals:chimerite_crystals"));
        for (BlockPos pos : crystalPos) {
            if(!context.getWorld().getBlockState(pos).getBlock().is(afTags)) {
                return new TranslationTextComponent("ritual.arcanefundamentals.no_crystal", pos.toShortString());
            }
        }
        Affinity neededAffinity = this.spellContext.getSpellAffinity().getOpposite();
        NonNullList<Entity> entities= getNearbyAffinityEntities(context);
        if (entities.size() < 4) 
            return new TranslationTextComponent("ritual.arcanefundamentals.no_affinity", neededAffinity);
        for (Entity e: entities) {
            if (!neededAffinity.equals(getEntityAffinity(e, context)))
                return new TranslationTextComponent("ritual.arcanefundamentals.wrong_affinity", neededAffinity);
        }
        return null;
    }
    
    @Override
    public boolean spawnRitualParticles(IRitualContext context) {
        if (tickCounter >= 198 && spellContext != null) {
            BlockPos center = context.getCenter();
            Vector3d me = new Vector3d(center.getX() + 0.5d, center.getY() + 1d, center.getZ() + 0.5d);
            Random rnd = new Random();
            if (this.findNearbyPoints(context)) {
                Affinity affinity = spellContext.getSpellAffinity().getOpposite();
                if (affinity == Affinity.ICE) affinity = Affinity.LIGHTNING;
                else if (affinity == Affinity.LIGHTNING) affinity = Affinity.ICE;
                ParticleType<?> instanceAffinity = AFParticleType.getCheckedInstance(ParticleGetter.getAffinityParticle(affinity));
                if (instanceAffinity != null) {
                    for (BlockPos pos : this.positions) {
                        Vector3d start = new Vector3d(pos.getX() + 0.5f, (pos.getY() + 0.5f), (pos.getZ() + 0.5f));
                        for (int i = 0; i < 5; ++i) {
                            context.getWorld().addParticle((IParticleData)instanceAffinity, start.x() - 0.15f + rnd.nextFloat() * 0.3f, start.y() - 0.15f + rnd.nextFloat() * 0.3f, start.z() - 0.15f + rnd.nextFloat() * 0.3f, me.x(), me.y(), me.z());
                        }
                    }
                }
            }
            if (this.getFullReservoirs(context)) {
                ParticleType<?> instanceMana = AFParticleType.getCheckedInstance(ParticleGetter.getParticle(ParticleGetter.SPARKLE_LERP_POINT));
                if (instanceMana != null) {
                    BlockPos[] reservoirPos = new BlockPos[] {context.getCenter().offset(3, 0, 0), context.getCenter().offset(-3, 0, 0), context.getCenter().offset(0, 0, -3), context.getCenter().offset(0, 0, 3)};
                    for (BlockPos pos : reservoirPos) {
                        Vector3d start = new Vector3d(pos.getX() + 0.5f, (pos.getY() + 0.3f), (pos.getZ() + 0.5f));
                        for (int i = 0; i < 5; ++i) {
                            context.getWorld().addParticle((IParticleData)instanceMana, start.x() - 0.15f + rnd.nextFloat() * 0.3f, start.y() - 0.15f + rnd.nextFloat() * 0.3f, start.z() - 0.15f + rnd.nextFloat() * 0.3f, me.x(), me.y(), me.z());
                        }
                    }
                }
            }
            return false;
        } else {
            tickCounter++;
            return false;
        }    
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
    
    private boolean findNearbyPoints(IRitualContext context) {
        List<Entity> entities = this.getNearbyAffinityEntities(context);
        if (entities.size() < 4) {
            return false;
        }
        this.positions.clear();
        entities.stream().forEach(e -> this.positions.add(new BlockPos(e.position())));
        return true;
    }
    
    private Affinity getEntityAffinity(Entity entity, IRitualContext context) {
        CompoundNBT nbt = entity.serializeNBT();
        if (nbt.contains("affinity")) {        
            return Affinity.valueOf(nbt.getString("affinity"));
        }
        else {
            ArcaneFundamentals.LOGGER.error("Failed to load affinity for Affinity Icon Entity");
            return Affinity.UNKNOWN;
        } 
    }
    
    private boolean getFullReservoirs(IRitualContext context) {
        BlockPos[] reservoirPos = new BlockPos[] {context.getCenter().offset(3, 0, 0), context.getCenter().offset(-3, 0, 0), context.getCenter().offset(0, 0, -3), context.getCenter().offset(0, 0, 3)};
        Block reservoir = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("mana-and-artifice:mana_resevoir"));
        for (BlockPos pos : reservoirPos) {
            if(!context.getWorld().getBlockState(pos).getBlock().is(reservoir) || context.getWorld().getBlockState(pos).getValue(IntegerProperty.create("fill_level", 0, 4)) != 4) {
                return false;
            }
        }
        return true;
    }
}