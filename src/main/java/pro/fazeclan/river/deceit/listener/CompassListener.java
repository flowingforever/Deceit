package pro.fazeclan.river.deceit.listener;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.jarona.util.GameUtil;

import java.util.Comparator;

public class CompassListener implements Listener {

    @EventHandler
    private void onCompassRightClick(PlayerInteractEvent event) {
        if (!GameUtil.hasGame(event.getPlayer().getWorld(), Deceit.getKey("murder"))) {
            return;
        }
        var player = event.getPlayer();
        var item = event.getItem();
        if (item == null) {
            return;
        }
        if (!item.getType().equals(Material.COMPASS)) {
            return;
        }
        var values = GameUtil.getGame(player.getWorld()).getGameValues(player.getWorld().getUID());
        if (player.hasCooldown(item)) {
            return;
        }
        player.setCooldown(item, 100);
        var tracked = player.getWorld().getPlayers()
                .stream()
                .filter(p -> !p.equals(player) && !p.getGameMode().isInvulnerable() && RoleUtil.isInnocent(p, values))
                .min(Comparator.comparingDouble(p -> p.getLocation().distanceSquared(player.getLocation())))
                .orElse(null);
        if (tracked == null) {
            return;
        }
        item.setData(DataComponentTypes.LODESTONE_TRACKER, LodestoneTracker.lodestoneTracker(
                tracked.getLocation().clone(),
                false
        ));
    }

}
