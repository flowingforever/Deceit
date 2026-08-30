package pro.fazeclan.river.deceit.ability.definitions.innocent;

import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.ability.Ability;
import pro.fazeclan.river.jarona.util.GameUtil;

public class ScalpelAbility extends Ability {

    public ScalpelAbility(Deceit plugin) {
        super(plugin, "scalpel");
    }

    @EventHandler
    private void onScalpelAttempt(EntityDamageByEntityEvent event) {
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
        if (!hasAbility(item)) {
            return;
        }
        event.setCancelled(true);
        if (attacker.hasCooldown(item)) {
            return;
        }
        attacker.setCooldown(item, getProperty("cooldown", 100));
        victim.heal(getProperty("heal", 3.0));
        victim.addPotionEffect(new PotionEffect(
                PotionEffectType.REGENERATION,
                getProperty("duration", 7) * 20,
                0,
                true,
                true,
                true
        ));
        victim.getWorld().spawnParticle(
                Particle.HEART,
                victim.getLocation(),
                10,
                1,
                1,
                1
        );
        victim.getWorld().playSound(
                victim.getLocation(),
                "minecraft:block.brewing_stand.brew",
                1f,
                1f
        );
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.RED_DYE.createItemStack();
    }

}
