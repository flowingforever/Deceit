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
                    .setItemProvider(item.getItemStack())
                    .addClickHandler((i, g, c) -> {
                        if (GameFunctions.buyIfPossible(player, values, item.getCost())) {
                            player.give(i.getItemProvider(player).get());
                        }
                        g.notifyWindows();
                    }).build());
        }

        Window.builder()
                .setUpperGui(gui)
                .setTitle("<yellow>Shop! " + values.getValue("coins_" + player.getUniqueId(), 0) + " coins.</yellow>")
                .open(player);
    }

}
