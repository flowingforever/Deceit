package pro.fazeclan.river.deceit.ability.definitions.evil;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.ability.Ability;
import pro.fazeclan.river.jarona.util.GameUtil;

public class FakeDaggerAbility extends Ability {

    public FakeDaggerAbility(Deceit plugin) {
        super(plugin, "fake_dagger");
    }

    @EventHandler
    private void onBackstabAttempt(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!GameUtil.hasGame(victim.getWorld(), Deceit.getKey("murder"))) return;
        var item = attacker.getInventory().getItemInMainHand();
        if (!hasAbility(item)) return;
        event.setDamage(0.0);
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.IRON_SWORD.createItemStack();
    }

}
