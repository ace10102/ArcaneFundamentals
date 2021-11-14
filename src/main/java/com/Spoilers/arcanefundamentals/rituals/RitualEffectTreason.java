package com.Spoilers.arcanefundamentals.rituals;

import com.Spoilers.arcanefundamentals.entities.RitualUtilityEntity;
import com.ma.api.ManaAndArtificeMod;
import com.ma.api.capabilities.Faction;
import com.ma.api.capabilities.IPlayerMagic;
import com.ma.api.capabilities.IPlayerProgression;
import com.ma.api.rituals.IRitualContext;
import com.ma.api.rituals.RitualEffect;
import com.ma.api.sound.SFX;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class RitualEffectTreason extends RitualEffect {

    public RitualEffectTreason(ResourceLocation ritualName) {
        super(ritualName);

    }

    @Override
    protected boolean applyRitualEffect(IRitualContext context) {
        PlayerEntity traitor = context.getCaster();
        if (traitor == null)
            return false;
        IPlayerProgression playerContext = traitor.getCapability(ManaAndArtificeMod.getProgressionCapability()).orElse(null);
        IPlayerMagic playerMagic = traitor.getCapability(ManaAndArtificeMod.getMagicCapability()).orElse(null);
        if (playerContext == null || playerMagic == null || !playerMagic.isMagicUnlocked())
            return false;
        Faction playerFac = playerContext.getAlliedFaction();
        if (playerFac == Faction.NONE)
            return false;
        int playerTier = playerContext.getTier();

        playerContext.setAlliedFaction(Faction.NONE, traitor);
        playerContext.increaseFactionStanding(-10000);
        playerContext.setTier(2, traitor);
        playerMagic.setMagicLevel(29);
        playerMagic.setMagicXP(playerMagic.getXPForLevel(29));
        playerMagic.getCastingResource().setAmount(playerMagic.getCastingResource().getMaxAmount());

        context.getWorld().playSound(null, context.getCenter().getX(), context.getCenter().getY(),
                context.getCenter().getZ(), playHornSound(playerFac), SoundCategory.PLAYERS, 1.0f, (float) (0.9f + 0.2f * Math.random()));

        RitualUtilityEntity ritualHandler = new RitualUtilityEntity(context.getWorld(),
                this.getRegistryName().toString(), context.getCenter().asLong(), traitor, playerTier);
        ritualHandler.setPos(context.getCenter().getX() + 0.5, context.getCenter().getY(), context.getCenter().getZ() + 0.5);
        ritualHandler.setFaction(playerFac);
        context.getWorld().addFreshEntity(ritualHandler);

        return true;
    }

    private SoundEvent playHornSound(Faction faction) {
        switch (faction) {
        case ANCIENT_WIZARDS: {
            return SFX.Event.Faction.FACTION_HORN_COUNCIL;
        }
        case DEMONS: {
            return SFX.Event.Faction.FACTION_HORN_DEMONS;
        }
        case FEY_COURT: {
            return SFX.Event.Faction.FACTION_HORN_FEY;
        }
        case UNDEAD: {
            return SFX.Event.Faction.FACTION_HORN_UNDEAD;
        }
        default:
            return SFX.Event.Faction.FACTION_HORN_COUNCIL;
        }
    }

    @Override
    protected int getApplicationTicks(IRitualContext context) {
        return 0;
    }

    @Override
    protected boolean modifyRitualReagentsAndPatterns(ItemStack dataStack, IRitualContext context) {
        PlayerEntity traitor = context.getCaster();
        if (traitor == null)
            return false;
        IPlayerProgression playerContext = traitor.getCapability(ManaAndArtificeMod.getProgressionCapability()).orElse(null);
        if (playerContext == null)
            return false;
        Faction playerFac = playerContext.getAlliedFaction();
        if (playerFac == Faction.NONE)
            return false;

        int playerTier = playerContext.getTier();
        ResourceLocation replaceFocus = playerTier == 5 ? new ResourceLocation("mana-and-artifice:ritual_focus_greater") :
            new ResourceLocation("mana-and-artifice:ritual_focus_lesser");
        ResourceLocation replaceArtifact;
        ResourceLocation replaceMark;
        ResourceLocation replaceMote;
        NonNullList<ResourceLocation> ritualWeaveList = NonNullList.create();
        switch (playerFac) {

        case ANCIENT_WIZARDS:
            replaceArtifact = playerTier == 5 ? new ResourceLocation("mana-and-artifice:faction_horn_council") :
                new ResourceLocation("mana-and-artifice:eldrin_bracelet");
            replaceMote = playerTier == 5 ? new ResourceLocation("mana-and-artifice:greater_mote_arcane") :
                new ResourceLocation("mana-and-artifice:mote_arcane");
            replaceMark = new ResourceLocation("mana-and-artifice:mark_of_the_council");
            ritualWeaveList = addCouncilWeaves(ritualWeaveList);
            break;
        case DEMONS:
            replaceArtifact = playerTier == 5 ? new ResourceLocation("mana-and-artifice:faction_horn_demons") :
                new ResourceLocation("mana-and-artifice:emberglow_bracelet");
            replaceMote = playerTier == 5 ? new ResourceLocation("mana-and-artifice:greater_mote_fire") :
                new ResourceLocation("mana-and-artifice:mote_fire");
            replaceMark = new ResourceLocation("mana-and-artifice:mark_of_the_nether");
            ritualWeaveList = addDemonWeaves(ritualWeaveList);
            break;
        case FEY_COURT:
            replaceArtifact = playerTier == 5 ? new ResourceLocation("mana-and-artifice:faction_horn_fey") :
                new ResourceLocation("mana-and-artifice:trickery_bracelet");
            replaceMote = playerTier == 5 ? new ResourceLocation("mana-and-artifice:greater_mote_air") :
                new ResourceLocation("mana-and-artifice:mote_air");
            replaceMark = new ResourceLocation("mana-and-artifice:mark_of_the_fey");
            ritualWeaveList = addFeyWeaves(ritualWeaveList);
            break;
        case UNDEAD:
            replaceArtifact = playerTier == 5 ? new ResourceLocation("mana-and-artifice:faction_horn_undead") :
                new ResourceLocation("mana-and-artifice:bone_ring");
            replaceMote = playerTier == 5 ? new ResourceLocation("mana-and-artifice:greater_mote_ender") :
                new ResourceLocation("mana-and-artifice:mote_ender");
            replaceMark = new ResourceLocation("mana-and-artifice:mark_of_the_undead"); 
            ritualWeaveList = addUndeadWeaves(ritualWeaveList);
            break;

        default:
            return false;
        }

        context.replaceReagents(new ResourceLocation("arcanefundamentals:dynamic_focus"), replaceFocus);
        context.replaceReagents(new ResourceLocation("arcanefundamentals:dynamic_faction"), replaceArtifact);
        context.replaceReagents(new ResourceLocation("arcanefundamentals:dynamic_mark"), replaceMark);
        context.replaceReagents(new ResourceLocation("arcanefundamentals:dynamic_mote"), replaceMote);
        context.replacePatterns(ritualWeaveList);

        return true;
    }

    private NonNullList<ResourceLocation> addCouncilWeaves(NonNullList<ResourceLocation> weaveList) {
        weaveList.add(new ResourceLocation("mana-and-artifice:manaweave_patterns/inverted_triangle"));
        weaveList.add(new ResourceLocation("mana-and-artifice:manaweave_patterns/square"));
        weaveList.add(new ResourceLocation("mana-and-artifice:manaweave_patterns/circle"));
        weaveList.add(new ResourceLocation("mana-and-artifice:manaweave_patterns/square"));
        weaveList.add(new ResourceLocation("mana-and-artifice:manaweave_patterns/triangle"));

        return weaveList;
    }

    private NonNullList<ResourceLocation> addDemonWeaves(NonNullList<ResourceLocation> weaveList) {
        weaveList.add(new ResourceLocation("mana-and-artifice:manaweave_patterns/inverted_triangle"));
        weaveList.add(new ResourceLocation("mana-and-artifice:manaweave_patterns/diamond"));
        weaveList.add(new ResourceLocation("mana-and-artifice:manaweave_patterns/inverted_triangle"));
        weaveList.add(new ResourceLocation("mana-and-artifice:manaweave_patterns/triangle"));

        return weaveList;
    }

    private NonNullList<ResourceLocation> addFeyWeaves(NonNullList<ResourceLocation> weaveList) {
        weaveList.add(new ResourceLocation("mana-and-artifice:manaweave_patterns/inverted_triangle"));
        weaveList.add(new ResourceLocation("mana-and-artifice:manaweave_patterns/circle"));
        weaveList.add(new ResourceLocation("mana-and-artifice:manaweave_patterns/inverted_triangle"));
        weaveList.add(new ResourceLocation("mana-and-artifice:manaweave_patterns/triangle"));

        return weaveList;
    }

    private NonNullList<ResourceLocation> addUndeadWeaves(NonNullList<ResourceLocation> weaveList) {
        weaveList.add(new ResourceLocation("mana-and-artifice:manaweave_patterns/inverted_triangle"));
        weaveList.add(new ResourceLocation("mana-and-artifice:manaweave_patterns/knot3"));
        weaveList.add(new ResourceLocation("mana-and-artifice:manaweave_patterns/diamond"));
        weaveList.add(new ResourceLocation("mana-and-artifice:manaweave_patterns/knot"));
        weaveList.add(new ResourceLocation("mana-and-artifice:manaweave_patterns/inverted_triangle"));

        return weaveList;
    }
}
