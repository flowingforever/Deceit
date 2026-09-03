package pro.fazeclan.river.deceit.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.apache.commons.lang3.tuple.Pair;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.util.MessageUtil;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ConfigCommand {

    public static Pair<LiteralArgumentBuilder<CommandSourceStack>, Collection<String>> command(Deceit plugin) {
        return Pair.of(
                Commands.literal("config")
                        .requires(ctx -> ctx.getSender().hasPermission("deceit.admin.config"))
                        .then(
                                Commands.literal("reload")
                                        .executes(ctx -> {
                                            plugin.reloadConfig();
                                            ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                    "<green>Reloaded main configuration!"
                                            ));

                                            return Command.SINGLE_SUCCESS;
                                        })
                        )
                        .then(
                                Commands.literal("reset")
                                        .executes(ctx -> {
                                            plugin.saveResource("config.yml", true);
                                            plugin.reloadConfig();
                                            ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                    "<green>Reset main configuration to defaults and reloaded!"
                                            ));

                                            return Command.SINGLE_SUCCESS;
                                        })
                        )
                        .then(
                                Commands.literal("set")
                                        .then(
                                                Commands.argument("entry", StringArgumentType.string())
                                                        .suggests((context, builder) -> {
                                                            plugin.getConfig().getValues(true)
                                                                    .entrySet()
                                                                    .stream()
                                                                    .filter(entry -> isNumerical(entry.getValue()))
                                                                    .map(Map.Entry::getKey)
                                                                    .forEach(builder::suggest);
                                                            return builder.buildFuture();
                                                        })
                                                        .then(
                                                                Commands.argument("integer", IntegerArgumentType.integer(0))
                                                                        .executes(ctx -> {
                                                                            var entry = ctx.getArgument("entry", String.class);
                                                                            var integer = ctx.getArgument("integer", Integer.class);
                                                                            plugin.getConfig().set(entry, integer);
                                                                            plugin.saveConfig();
                                                                            plugin.reloadConfig();

                                                                            ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                                                    "<green>Set " + entry + " to " + integer + "!"
                                                                            ));

                                                                            return Command.SINGLE_SUCCESS;
                                                                        })
                                                        ).then(
                                                                Commands.argument("double", DoubleArgumentType.doubleArg(0, 100))
                                                                        .executes(ctx -> {
                                                                            var entry = ctx.getArgument("entry", String.class);
                                                                            var aDouble = ctx.getArgument("double", Double.class);
                                                                            plugin.getConfig().set(entry, aDouble);
                                                                            plugin.saveConfig();
                                                                            plugin.reloadConfig();

                                                                            ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                                                    "<green>Set " + entry + " to " + aDouble + "!"
                                                                            ));

                                                                            return Command.SINGLE_SUCCESS;
                                                                        })
                                                        )
                                        )
                        )
                        .then(
                                Commands.literal("get")
                                        .then(
                                                Commands.argument("entry", StringArgumentType.string())
                                                        .suggests((context, builder) -> {
                                                            plugin.getConfig().getValues(true)
                                                                    .entrySet()
                                                                    .stream()
                                                                    .filter(entry -> isNumericalOrBoolean(entry.getValue()))
                                                                    .map(Map.Entry::getKey)
                                                                    .forEach(builder::suggest);
                                                            return builder.buildFuture();
                                                        })
                                                        .executes(ctx -> {
                                                            var value = ctx.getArgument("entry", String.class);
                                                            var configValue = plugin.getConfig().get(value);

                                                            if (configValue == null) {
                                                                ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                                        "<red>This entry has no value associated with it!"
                                                                ));
                                                                return Command.SINGLE_SUCCESS;
                                                            }

                                                            ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                                    "<green>" + value + " has a value of " + configValue + "!"
                                                            ));

                                                            return Command.SINGLE_SUCCESS;
                                                        })
                                        )
                        )
                        .then(ConfigAbilityCommand.command(plugin))
                        .then(ConfigRoleCommand.command(plugin))
                        .then(ConfigModifierCommand.command(plugin)),
                List.of("cf")
        );
    }

    private static boolean isNumerical(Object object) {
        return object instanceof Integer || object instanceof Double;
    }

    private static boolean isNumericalOrBoolean(Object object) {
        return isNumerical(object) || object instanceof Boolean;
    }

}
