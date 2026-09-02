package pro.fazeclan.river.deceit.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.apache.commons.lang3.tuple.Pair;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.ability.Ability;
import pro.fazeclan.river.deceit.command.argument.AbilityArgument;
import pro.fazeclan.river.deceit.command.argument.RoleArgument;
import pro.fazeclan.river.deceit.role.Role;
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
                        .then(
                                Commands.literal("ability")
                                        .then(
                                                Commands.argument("abilities", new AbilityArgument(plugin.getAbilityManager()))
                                                        .then(
                                                                Commands.literal("reload")
                                                                        .executes(ctx -> {
                                                                            var ability = ctx.getArgument("abilities", Ability.class);
                                                                            ability.reloadAbility();
                                                                            ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                                                    "<green>Reloaded ability " + ability.getId() + "'s configuration!"
                                                                            ));

                                                                            return Command.SINGLE_SUCCESS;
                                                                        })
                                                        )
                                                        .then(
                                                                Commands.literal("reset")
                                                                        .executes(ctx -> {
                                                                            var ability = ctx.getArgument("abilities", Ability.class);
                                                                            ability.resetAbility();
                                                                            ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                                                    "<green>Reset ability " + ability.getId() + "'s configuration to defaults and reloaded!"
                                                                            ));

                                                                            return Command.SINGLE_SUCCESS;
                                                                        })
                                                        )
                                                        .then(
                                                                Commands.literal("set")
                                                                        .then(
                                                                                Commands.argument("entry", StringArgumentType.string())
                                                                                        .suggests((ctx, builder) -> {
                                                                                            ctx.getArgument("abilities", Ability.class)
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
                                                                                                            var ability = ctx.getArgument("abilities", Ability.class);
                                                                                                            var entry = ctx.getArgument("entry", String.class);
                                                                                                            var integer = ctx.getArgument("integer", Integer.class);
                                                                                                            ability.getConfig().set(entry, integer);
                                                                                                            ability.saveAbility();
                                                                                                            ability.reloadAbility();

                                                                                                            ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                                                                                    "<green>Set " + entry + " in " + ability.getId() + " to " + integer + "!"
                                                                                                            ));

                                                                                                            return Command.SINGLE_SUCCESS;
                                                                                                        })
                                                                                        ).then(
                                                                                                Commands.argument("double", DoubleArgumentType.doubleArg(0, 100))
                                                                                                        .executes(ctx -> {
                                                                                                            var ability = ctx.getArgument("abilities", Ability.class);
                                                                                                            var entry = ctx.getArgument("entry", String.class);
                                                                                                            var aDouble = ctx.getArgument("double", Double.class);
                                                                                                            ability.getConfig().set(entry, aDouble);
                                                                                                            ability.saveAbility();
                                                                                                            ability.reloadAbility();

                                                                                                            ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                                                                                    "<green>Set " + entry + " in " + ability.getId() + " to " + aDouble + "!"
                                                                                                            ));

                                                                                                            return Command.SINGLE_SUCCESS;
                                                                                                        })
                                                                                        ).then(
                                                                                                Commands.argument("boolean", BoolArgumentType.bool())
                                                                                                        .executes(ctx -> {
                                                                                                            var ability = ctx.getArgument("abilities", Ability.class);
                                                                                                            var entry = ctx.getArgument("entry", String.class);
                                                                                                            var aBoolean = ctx.getArgument("boolean", Boolean.class);
                                                                                                            ability.getConfig().set(entry, aBoolean);
                                                                                                            ability.saveAbility();
                                                                                                            ability.reloadAbility();

                                                                                                            ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                                                                                    "<green>Set " + entry + " in " + ability.getId() + " to " + aBoolean + "!"
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
                                                                                            ctx.getArgument("abilities", Ability.class)
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
                                                                                            var ability = ctx.getArgument("abilities", Ability.class);
                                                                                            var value = ctx.getArgument("entry", String.class);
                                                                                            var configValue = ability.getConfig().get(value);

                                                                                            if (configValue == null) {
                                                                                                ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                                                                        "<red>This entry has no value associated with it!"
                                                                                                ));
                                                                                                return Command.SINGLE_SUCCESS;
                                                                                            }

                                                                                            ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                                                                    "<green>" + ability.getId() + "'s " + value + " has a value of " + configValue + "!"
                                                                                            ));

                                                                                            return Command.SINGLE_SUCCESS;
                                                                                        })
                                                                        )
                                                        )
                                        )
                        )
                        .then(
                                Commands.literal("role")
                                        .then(
                                                Commands.argument("roles", new RoleArgument(plugin.getRoleManager()))
                                                        .then(
                                                                Commands.literal("reload")
                                                                        .executes(ctx -> {
                                                                            var role = ctx.getArgument("roles", Role.class);
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
                                                                            var role = ctx.getArgument("roles", Role.class);
                                                                            role.resetRole();
                                                                            ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                                                    "<green>Reset ability " + role.getName() + "<reset><green>'s configuration to defaults and reloaded!"
                                                                            ));

                                                                            return Command.SINGLE_SUCCESS;
                                                                        })
                                                        )
                                                        .then(
                                                                Commands.literal("set")
                                                                        .then(
                                                                                Commands.argument("entry", StringArgumentType.string())
                                                                                        .suggests((ctx, builder) -> {
                                                                                            ctx.getArgument("roles", Role.class)
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
                                                                                                            var role = ctx.getArgument("roles", Role.class);
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
                                                                                                            var role = ctx.getArgument("roles", Role.class);
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
                                                                                                            var role = ctx.getArgument("roles", Role.class);
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
                                                                                            ctx.getArgument("roles", Role.class)
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
                                                                                            var role = ctx.getArgument("roles", Role.class);
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
                                        )
                        ),
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
