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
import pro.fazeclan.river.deceit.role.Role;
import pro.fazeclan.river.deceit.role.RoleManager;

import java.util.concurrent.CompletableFuture;

public class RoleArgument implements CustomArgumentType<Role, String> {

    private final RoleManager roleManager;

    public RoleArgument(RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    private static final SimpleCommandExceptionType ERROR_INVALID_KEY = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(Component.text("This game role key is not structured correctly!"))
    );

    private static final SimpleCommandExceptionType ERROR_REGISTRY_NO_KEY = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(Component.text("The game role registry does not have this role!"))
    );

    @Override
    public Role parse(StringReader reader) {
        throw new UnsupportedOperationException("This method will never be called.");
    }

    @Override
    public <S> Role parse(StringReader reader, S source) throws CommandSyntaxException {
        var registry = roleManager.getRegistry();

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
        roleManager.getRegistry().keySet()
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

}