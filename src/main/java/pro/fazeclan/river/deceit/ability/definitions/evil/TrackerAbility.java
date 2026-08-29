package pro.fazeclan.river.deceit.ability.definitions.evil;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.ability.Ability;
import pro.fazeclan.river.deceit.ability.AbilityEvent;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.jarona.util.GameUtil;

import java.util.Comparator;

public class TrackerAbility extends Ability {

    public TrackerAbility(Deceit plugin) {
        super(plugin, "tracker");
    }

    @EventHandler
    private void onTrackerUse(AbilityEvent event) {
        if (!event.getExpectedAbility().equals(getId())) return;
        var player = event.getPlayer();
        var item = event.getItemStack();

        var values = GameUtil.getGame(player.getWorld()).getGameValues(player.getWorld().getUID());
        player.setCooldown(item, getProperty("cooldown", 100));
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

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.COMPASS.createItemStack();
    }
}
