package pro.fazeclan.river.deceit.listener;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.ability.AbilityEvent;
import pro.fazeclan.river.jarona.util.GameUtil;

public class AbilityListener implements Listener {

    private final Deceit plugin;

    public AbilityListener(Deceit plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onAbilityItemInteract(PlayerInteractEvent event) {
        var player = event.getPlayer();
        if (!GameUtil.hasGame(player.getWorld(), Deceit.getKey("murder"))) return;
        if (player.getGameMode().equals(GameMode.SPECTATOR)) return;
        if (player.hasPotionEffect(PotionEffectType.UNLUCK)) return;
        var item = event.getItem();
        if (item == null) return;
        if (player.hasCooldown(item)) return;
        if (!item.getPersistentDataContainer().has(Deceit.getKey("ability"))) return;
        plugin.getServer().getPluginManager().callEvent(new AbilityEvent(
                player,
                item,
                item.getPersistentDataContainer().get(
                        Deceit.getKey("ability"),
                        PersistentDataType.STRING
                )
        ));
    }

}
