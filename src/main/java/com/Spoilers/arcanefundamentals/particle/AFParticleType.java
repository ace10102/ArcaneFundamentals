package com.Spoilers.arcanefundamentals.particle;

import java.lang.reflect.Constructor;

import com.Spoilers.arcanefundamentals.ArcaneFundamentals;

import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;

@SuppressWarnings("unchecked")
public class AFParticleType {
    private boolean isProbablyValid = false;
    private Class<?> MAClass;
    private Constructor<?> MAConstructor;
    private ParticleType<?> particleType;
    
    public static <E extends IParticleData> ParticleType<E> getCheckedInstance(ParticleType<?> type) {
        AFParticleType atype = new AFParticleType(type);
        ParticleType<?> instance = null;
        if (atype.isHopefullyValid()) {
            instance = atype.instantiate();
        }
        return (ParticleType<E>)instance;
    }

    public <T extends ParticleType<?>> AFParticleType(ParticleType<?> type) {
        try {
            if (type instanceof ParticleType) {
                this.isProbablyValid = true;
                this.MAClass = (Class<T>)type.getClass();
                this.MAConstructor = MAClass.getConstructor(ParticleType.class);
                this.particleType = type;
            }
        }catch(Exception ex) {
            ArcaneFundamentals.LOGGER.error("Could not Create AFParticleType");
        }
    }

    private <E extends IParticleData> ParticleType<E> instantiate() {
        try {
            return (ParticleType<E>)this.MAConstructor.newInstance(this.particleType);
        }catch(Exception ex) {
            ArcaneFundamentals.LOGGER.error("Could not get MAParticleType Constructor");
            return null;
        }
    }

    public boolean isHopefullyValid() {
        return isProbablyValid;
    }
}