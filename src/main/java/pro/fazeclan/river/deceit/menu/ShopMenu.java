package pro.fazeclan.river.deceit.menu;

import org.bukkit.entity.Player;
import pro.fazeclan.river.deceit.util.GameFunctions;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.jarona.game.GameValues;
import pro.fazeclan.river.jarona.invui.gui.Gui;
import pro.fazeclan.river.jarona.invui.item.BoundItem;
import pro.fazeclan.river.jarona.invui.window.Window;

public class ShopMenu {

    // TODO: Turn into Dialog menu
    public static void createAndShowMenu(Player player, GameValues values) {
        var role = RoleUtil.getRole(player, values);
        var items = role.getShopItems();
        var itemCount = items.size();
        var gui = Gui.empty(9, 1 + (int) Math.floor(itemCount / 9.0));

        for (var item : items) {
            gui.addItems(BoundItem.builder()
                    .setItemProvider(item.getDisplayItem())
                    .addClickHandler((i, g, c) -> {
                        if (GameFunctions.buyIfPossible(player, values, item.getCost())) {
                            for (var entry : item.getItemStacks()) {
                                player.give(entry);
                            }
                            createAndShowMenu(player, values);
                            player.playSound(
                                    player.getLocation(),
                                    "minecraft:block.enchantment_table.use",
                                    1f,
                                    2f
                            );
                        } else {
                            player.playSound(
                                    player.getLocation(),
                                    "minecraft:block.note_block.bit",
                                    1f,
                                    0.5f
                            );
                        }
                    }).build());
        }

        Window.builder()
                .setUpperGui(gui)
                .setTitle("<yellow>Shop! " + values.getValue("coins_" + player.getUniqueId(), 0) + " coins.</yellow>")
                .open(player);
    }

}
