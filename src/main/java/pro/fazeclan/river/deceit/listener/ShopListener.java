package pro.fazeclan.river.deceit.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.menu.inventory.ShopMenu;
import pro.fazeclan.river.jarona.util.GameUtil;

public class ShopListener implements Listener {

    @EventHandler
    private void onSwapHands(PlayerSwapHandItemsEvent event) {
        var player = event.getPlayer();
        var world = player.getWorld();
        if (!GameUtil.hasGame(world, Deceit.getKey("murder"))) {
            return;
        }
        var game = GameUtil.getGame(world);
        if (game == null) {
            return;
        }
        var values = game.getGameValues(world.getUID());
        if (values.getValue("intermission_phase", 300L) > 0) return;
        event.setCancelled(true);
        ShopMenu.createAndShowMenu(player, game.getGameValues(world.getUID()));
    }

}
