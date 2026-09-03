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
import pro.fazeclan.river.deceit.ability.Ability;
import pro.fazeclan.river.deceit.command.argument.AbilityArgument;
import pro.fazeclan.river.deceit.util.MessageUtil;

import java.util.Map;

public class ConfigAbilityCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> command(Deceit plugin) {
        return Commands.literal("abilities")
                .then(
                        Commands.argument("ability", new AbilityArgument(plugin.getAbilityManager()))
                                .then(
                                        Commands.literal("reload")
                                                .executes(ctx -> {
                                                    var ability = ctx.getArgument("ability", Ability.class);
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
                                                    var ability = ctx.getArgument("ability", Ability.class);
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
                                                                    ctx.getArgument("ability", Ability.class)
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
                                                                                    var ability = ctx.getArgument("ability", Ability.class);
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
                                                                                    var ability = ctx.getArgument("ability", Ability.class);
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
                                                                                    var ability = ctx.getArgument("ability", Ability.class);
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
                                                                    ctx.getArgument("ability", Ability.class)
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
                                                                    var ability = ctx.getArgument("ability", Ability.class);
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
                );
    }

    private static boolean isNumerical(Object object) {
        return object instanceof Integer || object instanceof Double;
    }

    private static boolean isNumericalOrBoolean(Object object) {
        return isNumerical(object) || object instanceof Boolean;
    }

}
