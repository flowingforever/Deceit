package pro.fazeclan.river.deceit.ability.definitions.evil;

import net.kyori.adventure.util.TriState;
import org.bukkit.entity.Mannequin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.ability.Ability;
import pro.fazeclan.river.deceit.util.GameFunctions;

public class TorchAbility extends Ability {

    public TorchAbility(Deceit plugin) {
        super(plugin, "torch");
    }

    @EventHandler
    private void onTorchCorpseInteraction(PlayerInteractAtEntityEvent event) {
        var player = event.getPlayer();
        var item = player.getEquipment().getItem(event.getHand());
        if (event.getPlayer().getGameMode().isInvulnerable()) return;
        if (!hasAbility(item)) return;
        if (!(event.getRightClicked() instanceof Mannequin corpse)) return;
        if (corpse.getVisualFire().equals(TriState.TRUE)) return;
        GameFunctions.burnCorpse(getPlugin(), corpse, getProperty("burn-time", 1));
    }

    @Override
    public ItemStack getDisplayItem() {
        return null;
    }

}
