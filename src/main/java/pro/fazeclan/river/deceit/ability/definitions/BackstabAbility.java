package pro.fazeclan.river.deceit.ability.definitions;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.util.Vector;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.ability.Ability;
import pro.fazeclan.river.jarona.util.GameUtil;

public class BackstabAbility extends Ability {
    public BackstabAbility(Deceit plugin) {
        super(plugin, "backstab");
    }

    @EventHandler
    private void onBackstabAttempt(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }
        if (!(event.getDamager() instanceof Player attacker)) {
            return;
        }
        if (!GameUtil.hasGame(victim.getWorld(), Deceit.getKey("murder"))) {
            return;
        }
        var item = attacker.getInventory().getItemInMainHand();
        if (attacker.hasCooldown(item)) {
            event.setDamage(0.0);
            return;
        }
        if (!hasAbility(item)) {
            return;
        }
        attacker.setCooldown(item, getProperty("cooldown", 100));
        if (!isBehindPlayer(attacker, victim)) {
            event.setDamage(event.getDamage() / 2.0);
            return;
        }
        event.setDamage(2000); // one tap pretty much
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.IRON_SWORD.createItemStack();
    }

    private boolean isBehindPlayer(Player attacker, Player victim) {
        // thank you sonicdude for this beautiful code so i don't have to look up everything
        Vector victimFacing = victim.getLocation()
                .getDirection()
                .setY(0);

        Vector victimToAttacker = attacker.getLocation()
                .toVector()
                .subtract(victim.getLocation().toVector())
                .setY(0);

        if (victimFacing.lengthSquared() == 0.0
                || victimToAttacker.lengthSquared() == 0.0) {
            return false;
        }

        victimFacing.normalize();
        victimToAttacker.normalize();

        double behindDot = victimFacing
                .multiply(-1)
                .dot(victimToAttacker);

        return behindDot >= 0.5;
    }
}
