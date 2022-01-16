package com.Spoilers.arcanefundamentals.commands;

import com.ma.api.affinity.Affinity;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TranslationTextComponent;

public class AFAffinityArgument implements ArgumentType<Affinity> {
    private static final Collection<String> EXAMPLES = Arrays.asList(Affinity.values()).stream().map(a -> a.name()).collect(Collectors.toList());
    public static final DynamicCommandExceptionType PART_BAD_ID = new DynamicCommandExceptionType(p_208696_0_ -> new TranslationTextComponent("argument.item.id.invalid", new Object[]{p_208696_0_}));

    public Affinity parse(StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        String inputString = reader.readString();
        try {
            return Affinity.valueOf(inputString);
        }
        catch (Exception exception) {
            reader.setCursor(i);
            throw PART_BAD_ID.createWithContext(reader, inputString.toString());
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        ArrayList<String> all = new ArrayList<String>();
        all.addAll(EXAMPLES);
        return ISuggestionProvider.suggest(all, builder);
    }

    public static <S> Affinity getAffinity(CommandContext<S> context, String name) {
        return (context.getArgument(name, Affinity.class));
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

