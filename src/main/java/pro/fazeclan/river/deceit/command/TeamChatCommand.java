package pro.fazeclan.river.deceit.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Player;
import pro.fazeclan.river.deceit.util.MessageUtil;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.jarona.util.GameUtil;
import pro.fazeclan.river.jarona.util.NicknameUtil;

import java.util.Collection;
import java.util.List;

public class TeamChatCommand {

    public static Pair<LiteralArgumentBuilder<CommandSourceStack>, Collection<String>> command() {
        return Pair.of(
                Commands.literal("teamchat")
                        .then(
                                Commands.argument("message", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            if (!(ctx.getSource().getExecutor() instanceof Player)) {
                                                ctx.getSource().getSender().sendMessage(MessageUtil.formatComponent(
                                                        "<red>This must be a player executing this command!"
                                                ));
                                                return Command.SINGLE_SUCCESS;
                                            }
                                            var player = ctx.getSource().getPlayerOrThrow();
                                            var game = GameUtil.getGame(player);
                                            if (game == null) {
                                                player.sendMessage(MessageUtil.formatComponent(
                                                        "<red>You must be in a game in order to do this!"
                                                ));
                                                return Command.SINGLE_SUCCESS;
                                            }
                                            var values = game.getGameValues(player.getWorld().getUID());
                                            var role = RoleUtil.getRole(player, values);
                                            if (role == null) {
                                                return Command.SINGLE_SUCCESS;
                                            }
                                            if (RoleUtil.isInnocent(player, values)) {
                                                player.sendMessage(MessageUtil.formatComponent(
                                                        "<red>You cannot do this as an innocent player."
                                                ));
                                                return Command.SINGLE_SUCCESS;
                                            }
                                            var team = RoleUtil.getAllInTeam(player, player.getWorld().getPlayers(), values);
                                            var mm = MiniMessage.miniMessage();
                                            var message = ctx.getArgument("message", String.class);
                                            for (var tm : team) {
                                                tm.sendMessage(mm.deserialize(
                                                        "<head:" + player.getUniqueId() + "> <hover:show_text:'<gray>" + player.getName() + "</gray>'>" + NicknameUtil.getNickname(player) + "<reset><" + role.getMiniMessageColor() + ">: "
                                                                + message
                                                ));
                                                tm.playSound(
                                                        tm.getLocation(),
                                                        "minecraft:block.note_block.bit",
                                                        1f,
                                                        1f
                                                );
                                            }
                                            return Command.SINGLE_SUCCESS;
                                        })
                        ),
                List.of("tc", "tchat")
        );
    }

}
