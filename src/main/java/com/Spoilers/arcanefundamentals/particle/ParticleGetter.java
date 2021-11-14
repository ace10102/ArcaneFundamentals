package com.Spoilers.arcanefundamentals.particle;

import com.ma.api.affinity.Affinity;

import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class ParticleGetter {
  //lerps
    public static final ResourceLocation ARCANE_LERP = new ResourceLocation("mana-and-artifice:arcane_lerp");
    public static final ResourceLocation DUST_LERP = new ResourceLocation("mana-and-artifice:dust_lerp");
    public static final ResourceLocation ENDER = new ResourceLocation("mana-and-artifice:ender");
    public static final ResourceLocation FLAME_LERP = new ResourceLocation("mana-and-artifice:flame_lerp");
    public static final ResourceLocation LIGHTNING_BOLT = new ResourceLocation("mana-and-artifice:lightning_bolt");
    public static final ResourceLocation WATER_LERP = new ResourceLocation("mana-and-artifice:water_lerp");
    public static final ResourceLocation FROST_LERP = new ResourceLocation("mana-and-artifice:frost_lerp");
    public static final ResourceLocation AIR_LERP = new ResourceLocation("mana-and-artifice:air_lerp");
    public static final ResourceLocation SPARKLE_LERP_POINT = new ResourceLocation("mana-and-artifice:sparkle_lerp_point");
    
    //basic
    public static final ResourceLocation HELLFIRE = new ResourceLocation("mana-and-artifice:hellfire");
    public static final ResourceLocation FROST = new ResourceLocation("mana-and-artifice:frost");
    public static final ResourceLocation BONE = new ResourceLocation("mana-and-artifice:bone");
    
    //other
    public static final ResourceLocation BLUE_SPARKLE_GRAVITY = new ResourceLocation("mana-and-artifice:blue_sparkle_gravity");
    public static final ResourceLocation LIGHT_VELOCITY = new ResourceLocation("mana-and-artifice:light_velocity");
    
    public static ParticleType<?> getParticle(ResourceLocation particle) {
        return ForgeRegistries.PARTICLE_TYPES.getValue(particle);
    }
    
    public static ParticleType<?> getAffinityParticle(Affinity affinity) {
        switch (affinity) {
        case ARCANE: return getParticle(ARCANE_LERP);
        case EARTH: return getParticle(DUST_LERP);
        case ENDER: return getParticle(ENDER);
        case FIRE: return getParticle(FLAME_LERP);
        case LIGHTNING: return getParticle(LIGHTNING_BOLT);
        case WATER: return getParticle(WATER_LERP);
        case ICE: return getParticle(FROST_LERP);
        case WIND: return getParticle(AIR_LERP);
        default: return getParticle(SPARKLE_LERP_POINT);
        }
    }
}
