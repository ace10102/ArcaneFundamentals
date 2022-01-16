package com.Spoilers.arcanefundamentals.commands;

import java.util.Optional;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.ma.api.ManaAndArtificeMod;
import com.ma.api.affinity.Affinity;
import com.ma.api.capabilities.WellspringNode;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;

public class CommandReplaceWellspring {

  public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(((((Commands.literal("replacewellspring")
              .requires(commandSource -> commandSource.hasPermission(2)))
              .then(Commands.argument("position", BlockPosArgument.blockPos())
                      .executes(context -> replaceWellspring(context.getSource(), BlockPosArgument.getOrLoadBlockPos(context, "position")))))
              .then(Commands.argument("position", BlockPosArgument.blockPos())
                      .then(Commands.argument("affinity", new AFAffinityArgument())
                              .executes(context -> replaceWellspring(context.getSource(), BlockPosArgument.getOrLoadBlockPos(context, "position"), AFAffinityArgument.getAffinity(context, "affinity"))))))
              .then(Commands.argument("position", BlockPosArgument.blockPos())
                      .then(Commands.argument("affinity", new AFAffinityArgument())
                              .then(Commands.argument("strength", FloatArgumentType.floatArg(5.0F, 25.0F))
                                      .executes(context -> replaceWellspring(context.getSource(), BlockPosArgument.getOrLoadBlockPos(context, "position"), AFAffinityArgument.getAffinity(context, "affinity"), FloatArgumentType.getFloat(context, "strength")))))))
              .then(Commands.argument("position", BlockPosArgument.blockPos())
                      .then(Commands.argument("affinity", new AFAffinityArgument())
                              .then(Commands.literal("add")
                                      .then(Commands.argument("strength", FloatArgumentType.floatArg(-25.0F, 25.0F))
                                              .executes(context -> replaceWellspring(context.getSource(), BlockPosArgument.getOrLoadBlockPos(context, "position"), AFAffinityArgument.getAffinity(context, "affinity"), FloatArgumentType.getFloat(context, "strength"), true)))))));
  }
  private static int replaceWellspring(CommandSource source, BlockPos position) {
      return replaceWellspring(source, position, null, 0F, false);
  }

  private static int replaceWellspring(CommandSource source, BlockPos position, Affinity affinity) {
      return replaceWellspring(source, position, affinity, 0F, false);
  }
  
  private static int replaceWellspring(CommandSource source, BlockPos position, Affinity affinity, float strength) {
      return replaceWellspring(source, position, affinity, strength, false);
  }

  private static int replaceWellspring(CommandSource source, BlockPos position, Affinity affinity, float strength, boolean add) {
      MutableBoolean success = new MutableBoolean(true);
      source.getLevel().getCapability(ManaAndArtificeMod.getWorldMagicCapability()).ifPresent(m -> {
          Optional<WellspringNode> existingWellspring = m.getWellspringRegistry().getNodeAt(position);
          if (!existingWellspring.isPresent()) {
              source.sendFailure(new TranslationTextComponent("general.arcanefundamentals.no_wellspring", position));
              success.setFalse();
              return;
          }
          CompoundNBT newWellspringData = new CompoundNBT();
          existingWellspring.get().writeToNBT(newWellspringData);

          if (strength != 0)
              newWellspringData.putFloat("strength", add ? (MathHelper.clamp(existingWellspring.get().getStrength() + strength, 0F, 25F)) : strength);
          if (affinity != null)
              newWellspringData.putInt("affinity", affinity.ordinal());

          WellspringNode newWellspring = WellspringNode.fromNBT(newWellspringData);
          if (!m.getWellspringRegistry().addNode(position, newWellspring, true)) {
              source.sendFailure(new TranslationTextComponent("mana-and-artifice.commands.wellspring.failed", new Object[] {position.toShortString()}));
              success.setFalse();
              return;
          }
      });
      return 0;
  }
}
