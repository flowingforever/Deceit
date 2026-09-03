package pro.fazeclan.river.deceit.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.command.argument.ModifierArgument;
import pro.fazeclan.river.deceit.modifier.Modifier;
import pro.fazeclan.river.deceit.util.MessageUtil;

import java.util.Map;

public class ConfigModifierCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command(Deceit plugin) {
        return Commands.literal("modifiers")
                .then(
                        Commands.argument("modifier", new ModifierArgument(plugin.getModifierManager()))
                                .then(
                                        Commands.literal("reload")
                                                .executes(ctx -> {
                                                    var mod = ctx.getArgument("modifier", Modifier.class);
                                                    mod.reload();
                                                    ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                            "<green>Reloaded modifier " + mod.getId() + "<reset><green>'s configuration!"
                                                    ));

                                                    return Command.SINGLE_SUCCESS;
                                                })
                                )
                                .then(
                                        Commands.literal("reset")
                                                .executes(ctx -> {
                                                    var mod = ctx.getArgument("modifier", Modifier.class);
                                                    mod.reset();
                                                    ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                            "<green>Reset modifier " + mod.getId() + "<reset><green>'s configuration to defaults and reloaded!"
                                                    ));

                                                    return Command.SINGLE_SUCCESS;
                                                })
                                )
                                .then(
                                        Commands.literal("set")
                                                .then(
                                                        Commands.argument("entry", StringArgumentType.string())
                                                                .suggests((ctx, builder) -> {
                                                                    ctx.getArgument("modifier", Modifier.class)
                                                                            .getConfig()
                                                                            .getValues(true)
                                                                            .entrySet()
                                                                            .stream()
                                                                            .filter(entry -> isNumericalOrBoolean(entry.getValue()))
                                                                            .map(Map.Entry::getKey)
                                                                            .forEach(builder::suggest);
                                                                    return builder.buildFuture();
                                                                })
                                                                .then(
                                                                        Commands.argument("integer", IntegerArgumentType.integer(0))
                                                                                .executes(ctx -> {
                                                                                    var mod = ctx.getArgument("modifier", Modifier.class);
                                                                                    var entry = ctx.getArgument("entry", String.class);
                                                                                    var integer = ctx.getArgument("integer", Integer.class);
                                                                                    mod.getConfig().set(entry, integer);
                                                                                    mod.save();
                                                                                    mod.reload();

                                                                                    ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                                                            "<green>Set " + entry + " in " + mod.getId() + "<reset><green> to " + integer + "!"
                                                                                    ));

                                                                                    return Command.SINGLE_SUCCESS;
                                                                                })
                                                                ).then(
                                                                        Commands.argument("double", DoubleArgumentType.doubleArg(0, 100))
                                                                                .executes(ctx -> {
                                                                                    var mod = ctx.getArgument("modifier", Modifier.class);
                                                                                    var entry = ctx.getArgument("entry", String.class);
                                                                                    var aDouble = ctx.getArgument("double", Double.class);
                                                                                    mod.getConfig().set(entry, aDouble);
                                                                                    mod.save();
                                                                                    mod.reload();

                                                                                    ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                                                            "<green>Set " + entry + " in " + mod.getId() + "<reset><green> to " + aDouble + "!"
                                                                                    ));

                                                                                    return Command.SINGLE_SUCCESS;
                                                                                })
                                                                ).then(
                                                                        Commands.argument("boolean", BoolArgumentType.bool())
                                                                                .executes(ctx -> {
                                                                                    var mod = ctx.getArgument("modifier", Modifier.class);
                                                                                    var entry = ctx.getArgument("entry", String.class);
                                                                                    var aBoolean = ctx.getArgument("boolean", Boolean.class);
                                                                                    mod.getConfig().set(entry, aBoolean);
                                                                                    mod.save();
                                                                                    mod.reload();

                                                                                    ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                                                            "<green>Set " + entry + " in " + mod.getId() + "<reset><green> to " + aBoolean + "!"
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
                                                                .suggests((ctx, builder) -> {
                                                                    ctx.getArgument("modifier", Modifier.class)
                                                                            .getConfig()
                                                                            .getValues(true)
                                                                            .entrySet()
                                                                            .stream()
                                                                            .filter(entry -> isNumericalOrBoolean(entry.getValue()))
                                                                            .map(Map.Entry::getKey)
                                                                            .forEach(builder::suggest);
                                                                    return builder.buildFuture();
                                                                })
                                                                .executes(ctx -> {
                                                                    var mod = ctx.getArgument("modifier", Modifier.class);
                                                                    var value = ctx.getArgument("entry", String.class);
                                                                    var configValue = mod.getConfig().get(value);

                                                                    if (configValue == null) {
                                                                        ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                                                "<red>This entry has no value associated with it!"
                                                                        ));
                                                                        return Command.SINGLE_SUCCESS;
                                                                    }

                                                                    ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                                            "<green>" + mod.getId() + "<reset><green>'s " + value + " has a value of " + configValue + "!"
                                                                    ));

                                                                    return Command.SINGLE_SUCCESS;
                                                                })
                                                )
                                )
                );
    }

    private static boolean isNumerical(Object object) {
        return object instanceof Integer || object instanceof Double;
    }

    private static boolean isNumericalOrBoolean(Object object) {
        return isNumerical(object) || object instanceof Boolean;
    }

}
