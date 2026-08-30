package pro.fazeclan.river.deceit.ability.definitions.evil;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.SulfurCube;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.ability.Ability;
import pro.fazeclan.river.deceit.ability.AbilityEvent;
import pro.fazeclan.river.deceit.util.ExplosiveUtil;

public class ExplosiveAbility extends Ability {
    public ExplosiveAbility(Deceit plugin) {
        super(plugin, "explosive");
    }

    @EventHandler
    private void onExplosiveUse(AbilityEvent event) {
        if (!event.getExpectedAbility().equals(getId())) return;
        var player = event.getPlayer();
        var item = event.getItemStack();

        item.setAmount(item.getAmount() - 1);
        player.setCooldown(item, getProperty("cooldown", 0));
        var world = player.getWorld();
        player.playSound(player.getLocation(), "minecraft:block.note_block.bit", 1.0f, 1.0f);
        world.spawn(player.getLocation(), SulfurCube.class, sc -> {
            sc.setAI(false);
            sc.setAware(false);
            sc.getEquipment().setItem(EquipmentSlot.BODY, ItemType.TNT.createItemStack());
            sc.setVelocity(player.getLocation().getDirection());
            sc.addPotionEffect(new PotionEffect(
                    PotionEffectType.INVISIBILITY,
                    -1,
                    0,
                    true,
                    false
            ));
            sc.getAttribute(Attribute.SCALE).setBaseValue(0.5);
            var loc = sc.getLocation();
            final double radius = getProperty("radius", 3.5);
            double damage = getProperty("damage", 20.0);
            double minimumDamage = getProperty("minimum-damage", 5.0);
            double velocityMultiplier = getProperty("velocity-multiplier", 1.1);
            int delay = getProperty("delay", 5);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!loc.getNearbyPlayers(radius).isEmpty()) {
                        ExplosiveUtil.handleExplosionBuildup(getPlugin(), player, loc, radius, damage, minimumDamage, velocityMultiplier, delay);
                        getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), sc::remove, delay);
                        cancel();
                    }
                }
            }.runTaskTimer(getPlugin(), 60, 2);
        });
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.TNT.createItemStack();
    }
}
