package com.Spoilers.arcanefundamentals.rituals;

import com.ma.api.rituals.IRitualContext;
import com.ma.api.rituals.RitualEffect;

import net.minecraft.util.ResourceLocation;

public class RitualEffectDecryptSpell extends RitualEffect {

	public RitualEffectDecryptSpell(ResourceLocation ritualName) {
		super(ritualName);
	}

	@Override
	protected boolean applyRitualEffect(IRitualContext context) {
		return false;
	}

	@Override
	protected int getApplicationTicks(IRitualContext context) {
		return 0;
	}

}
