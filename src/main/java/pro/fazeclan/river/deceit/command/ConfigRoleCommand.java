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
import pro.fazeclan.river.deceit.command.argument.RoleArgument;
import pro.fazeclan.river.deceit.role.Role;
import pro.fazeclan.river.deceit.util.MessageUtil;

import java.util.Map;

public class ConfigRoleCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command(Deceit plugin) {
        return Commands.literal("roles")
                .then(
                        Commands.argument("role", new RoleArgument(plugin.getRoleManager()))
                                .then(
                                        Commands.literal("reload")
                                                .executes(ctx -> {
                                                    var role = ctx.getArgument("role", Role.class);
                                                    role.reloadRole();
                                                    ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                            "<green>Reloaded role " + role.getName() + "<reset><green>'s configuration!"
                                                    ));

                                                    return Command.SINGLE_SUCCESS;
                                                })
                                )
                                .then(
                                        Commands.literal("reset")
                                                .executes(ctx -> {
                                                    var role = ctx.getArgument("role", Role.class);
                                                    role.resetRole();
                                                    ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                            "<green>Reset role " + role.getName() + "<reset><green>'s configuration to defaults and reloaded!"
                                                    ));

                                                    return Command.SINGLE_SUCCESS;
                                                })
                                )
                                .then(
                                        Commands.literal("set")
                                                .then(
                                                        Commands.argument("entry", StringArgumentType.string())
                                                                .suggests((ctx, builder) -> {
                                                                    ctx.getArgument("role", Role.class)
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
                                                                                    var role = ctx.getArgument("role", Role.class);
                                                                                    var entry = ctx.getArgument("entry", String.class);
                                                                                    var integer = ctx.getArgument("integer", Integer.class);
                                                                                    role.getConfig().set(entry, integer);
                                                                                    role.saveRole();
                                                                                    role.reloadRole();

                                                                                    ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                                                            "<green>Set " + entry + " in " + role.getName() + "<reset><green> to " + integer + "!"
                                                                                    ));

                                                                                    return Command.SINGLE_SUCCESS;
                                                                                })
                                                                ).then(
                                                                        Commands.argument("double", DoubleArgumentType.doubleArg(0, 100))
                                                                                .executes(ctx -> {
                                                                                    var role = ctx.getArgument("role", Role.class);
                                                                                    var entry = ctx.getArgument("entry", String.class);
                                                                                    var aDouble = ctx.getArgument("double", Double.class);
                                                                                    role.getConfig().set(entry, aDouble);
                                                                                    role.saveRole();
                                                                                    role.reloadRole();

                                                                                    ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                                                            "<green>Set " + entry + " in " + role.getName() + "<reset><green> to " + aDouble + "!"
                                                                                    ));

                                                                                    return Command.SINGLE_SUCCESS;
                                                                                })
                                                                ).then(
                                                                        Commands.argument("boolean", BoolArgumentType.bool())
                                                                                .executes(ctx -> {
                                                                                    var role = ctx.getArgument("role", Role.class);
                                                                                    var entry = ctx.getArgument("entry", String.class);
                                                                                    var aBoolean = ctx.getArgument("boolean", Boolean.class);
                                                                                    role.getConfig().set(entry, aBoolean);
                                                                                    role.saveRole();
                                                                                    role.reloadRole();

                                                                                    ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                                                            "<green>Set " + entry + " in " + role.getName() + "<reset><green> to " + aBoolean + "!"
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
                                                                    ctx.getArgument("role", Role.class)
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
                                                                    var role = ctx.getArgument("role", Role.class);
                                                                    var value = ctx.getArgument("entry", String.class);
                                                                    var configValue = role.getConfig().get(value);

                                                                    if (configValue == null) {
                                                                        ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                                                "<red>This entry has no value associated with it!"
                                                                        ));
                                                                        return Command.SINGLE_SUCCESS;
                                                                    }

                                                                    ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                                            "<green>" + role.getName() + "<reset><green>'s " + value + " has a value of " + configValue + "!"
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
