package pro.fazeclan.river.deceit.menu.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.body.PlainMessageDialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.role.Role;
import pro.fazeclan.river.deceit.role.definitions.innocent.SwapperRole;
import pro.fazeclan.river.jarona.condition.TimedCondition;
import pro.fazeclan.river.jarona.game.GameValues;
import pro.fazeclan.river.jarona.util.NicknameUtil;

import java.util.List;

public class SwapperDialogMenu {

    public static void openMenu(Player player, World world, GameValues values, TimedCondition condition, int cooldown, @Nullable Player p1) {
        var mm = MiniMessage.miniMessage();
        SwapperRole role = (SwapperRole) Deceit.getInstance().getRoleManager().getRole("swapper");
        List<PlainMessageDialogBody> options = world.getPlayers()
                        .stream()
                        .filter(p -> !values.getValue("revealed_" + p.getUniqueId(), false))
                        .map(p -> {
                            var nickname = NicknameUtil.getNickname(p);
                            if (p.equals(p1)) {
                                nickname += " " + role.getPrefix();
                            }
                            return DialogBody.plainMessage(
                                    mm.deserialize("<head:" + p.getUniqueId() + "> " + nickname)
                                            .clickEvent(ClickEvent.callback(a -> {
                                                if (p1 != null) {
                                                    condition.setDuration(cooldown * 20L);
                                                    condition.setHudCondition((c, _) -> !c.getAvailable());
                                                    player.closeInventory();
                                                    swapPositions(p1, p, role);
                                                } else {
                                                    openMenu(player, world, values, condition, cooldown, p);
                                                }
                                            }))
                            );
                        }).toList();
        player.showDialog(Dialog.create(
                builder -> builder.empty()
                        .base(
                                DialogBase.builder(mm.deserialize(text(role, "Select Two Players to Swap!")))
                                        .body(options)
                                        .afterAction(DialogBase.DialogAfterAction.NONE)
                                        .pause(false)
                                        .build()
                        )
                        .type(
                                DialogType.notice(
                                        ActionButton.builder(Component.text("Cancel").color(NamedTextColor.RED))
                                                .build()
                                )
                        )
        ));
    }

    private static void swapPositions(Player p1, Player p2, Role role) {
        var loc1 = p1.getLocation().clone();
        var loc2 = p2.getLocation().clone();
        p1.teleport(loc2);
        p2.teleport(loc1);

        var message = MiniMessage.miniMessage().deserialize(role.getPrefix() + " You've swapped locations with another player!");
        p1.sendMessage(message);
        p2.sendMessage(message);
    }

    private static String text(SwapperRole role, String text) {
        return "<" + role.getMiniMessageColor() + ">" + text + "</" + role.getMiniMessageColor() + ">";
    }

}
