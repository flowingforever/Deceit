package pro.fazeclan.river.deceit.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import pro.fazeclan.river.deceit.ability.Ability;
import pro.fazeclan.river.deceit.ability.AbilityManager;

import java.util.concurrent.CompletableFuture;

public class AbilityArgument implements CustomArgumentType<Ability, String> {

    private final AbilityManager manager;

    public AbilityArgument(AbilityManager manager) {
        this.manager = manager;
    }

    private static final SimpleCommandExceptionType ERROR_INVALID_KEY = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(Component.text("This game ability key is not structured correctly!"))
    );

    private static final SimpleCommandExceptionType ERROR_REGISTRY_NO_KEY = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(Component.text("The game ability registry does not have this role!"))
    );

    @Override
    public Ability parse(StringReader reader) {
        throw new UnsupportedOperationException("This method will never be called.");
    }

    @Override
    public <S> Ability parse(StringReader reader, S source) throws CommandSyntaxException {
        var registry = manager.getRegistry();

        final String key = getNativeType().parse(reader);
        if (key == null) {
            throw ERROR_INVALID_KEY.create();
        }

        if (!registry.containsKey(key)) {
            throw ERROR_REGISTRY_NO_KEY.create();
        }

        return registry.get(key);
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.string();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        manager.getRegistry().keySet()
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

}