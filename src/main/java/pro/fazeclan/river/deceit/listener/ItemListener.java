package pro.fazeclan.river.deceit.listener;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.SulfurCube;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.jarona.util.GameUtil;

import java.util.Comparator;

public class ItemListener implements Listener {

    private final Deceit plugin;

    public ItemListener(Deceit plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onCompassClick(PlayerInteractEvent event) {
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
        if (player.hasCooldown(item)) {
            return;
        }
        var values = GameUtil.getGame(player.getWorld()).getGameValues(player.getWorld().getUID());
        player.setCooldown(item, plugin.getConfig().getInt("compass-cooldown"));
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

    @EventHandler
    private void onGrenadeClick(PlayerInteractEvent event) {
        if (!GameUtil.hasGame(event.getPlayer().getWorld(), Deceit.getKey("murder"))) {
            return;
        }
        var player = event.getPlayer();
        var item = event.getItem();
        if (item == null) {
            return;
        }
        if (!item.getPersistentDataContainer().has(Deceit.getKey("grenade"))) {
            return;
        }
        if (player.hasCooldown(item)) {
            return;
        }
        player.setCooldown(item, plugin.getConfig().getInt("grenade-cooldown"));
        var world = player.getWorld();
        world.playSound(player.getLocation(), "minecraft:entity.creeper.hurt", 1.0f, 1.0f);
        world.spawn(player.getEyeLocation(), SulfurCube.class, sc -> {
            sc.setAware(false);
            sc.getEquipment().setItem(EquipmentSlot.BODY, ItemType.CREEPER_HEAD.createItemStack());
            sc.setVelocity(player.getLocation().getDirection());
            sc.addPotionEffect(new PotionEffect(
                    PotionEffectType.INVISIBILITY,
                    -1,
                    0,
                    true,
                    false
            ));
            sc.getAttribute(Attribute.SCALE).setBaseValue(0.5);
            sc.getAttribute(Attribute.AIR_DRAG_MODIFIER).setBaseValue(0.5);
            sc.getAttribute(Attribute.GRAVITY).setBaseValue(0.05);
            sc.getAttribute(Attribute.BOUNCINESS).setBaseValue(1);

            final double radius = plugin.getConfig().getDouble("grenade-radius", 5.0);
            final double damage = plugin.getConfig().getDouble("grenade-damage", 20.0);
            final double minimumDamage = plugin.getConfig().getDouble("grenade-minimum-damage", 5.0);
            final double velocityMultiplier = plugin.getConfig().getDouble("grenade-velocity-multiplier", 1.1);
            int delay = plugin.getConfig().getInt("grenade-delay", 5);
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (!sc.isValid()) {
                    return;
                }

                var loc = sc.getLocation().clone();
                sc.setGravity(false);
                sc.setVelocity(new Vector());
                handleExplosionBuildup(player, loc, radius, damage, minimumDamage, velocityMultiplier, delay);
                plugin.getServer().getScheduler().runTaskLater(plugin, sc::remove, plugin.getConfig().getInt("grenade-delay", 5));
            }, 60);
        });

    }

    private void handleExplosionBuildup(Player thrower, Location location, double radius, double damage, double minDmg, double veloMultiplier, int delay) {
        var world = thrower.getWorld().getUID();

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                tick++;

                thrower.getWorld().playSound(location, "minecraft:block.note_block.bit", SoundCategory.PLAYERS, 1.0f, 2.0f);
                if (tick >= delay) {
                    cancel();
                }
            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                if (Bukkit.getWorld(world) == null) {
                    return;
                }
                handleExplosion(thrower, location, radius, damage, minDmg, veloMultiplier);
                super.cancel();
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private void handleExplosion(Player thrower, Location location, double radius, double damage, double minDmg, double veloMultiplier) {
        createExplosionEffect(location);
        damageNearbyPlayers(thrower, location, radius, damage, minDmg, veloMultiplier);
    }

    private void createExplosionEffect(Location location) {
        double offset = plugin.getConfig().getDouble("grenade-radius", 5.0) / 2.0;

        location.getWorld().spawnParticle(
                Particle.EXPLOSION,
                location,
                15,
                offset,
                2,
                offset
        );

        location.getWorld().playSound(
                location,
                Sound.ENTITY_GENERIC_EXPLODE,
                1.0F,
                0.7F
        );
    }

    private void damageNearbyPlayers(Player thrower, Location location, double radius, double damage, double minDmg, double veloMultiplier) {
        for (Player nearbyPlayer : location.getNearbyPlayers(radius)) {
            if (nearbyPlayer.getGameMode().isInvulnerable()) {
                continue;
            }

            var distance = location.distance(nearbyPlayer.getLocation());
            var distanceMultiplier = (radius - distance) * veloMultiplier;
            var push = location.toVector().subtract(nearbyPlayer.getLocation().toVector()).multiply(-1);
            if (push.lengthSquared() > 0) {
                push.normalize();
            } else {
                push = new Vector(0, 1, 0);
            }
            push.add(new Vector(0, 0.2, 0));

            push.multiply(distanceMultiplier);

            nearbyPlayer.damage(Math.max(damage / distance, minDmg), thrower);
            nearbyPlayer.setVelocity(push);
        }
    }

    @EventHandler
    private void onPlantedExplosiveClick(PlayerInteractEvent event) {
        if (!GameUtil.hasGame(event.getPlayer().getWorld(), Deceit.getKey("murder"))) {
            return;
        }
        var player = event.getPlayer();
        var item = event.getItem();
        if (item == null) {
            return;
        }
        if (!item.getPersistentDataContainer().has(Deceit.getKey("explosive"))) {
            return;
        }
        if (player.hasCooldown(item)) {
            return;
        }
        player.getInventory().removeItemAnySlot(item);
        var config = plugin.getConfig();
        player.setCooldown(item, config.getInt("explosive-cooldown", 0));
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
            final double radius = config.getDouble("explosive-radius", 3.5);
            double damage = plugin.getConfig().getDouble("explosive-damage", 20.0);
            double minimumDamage = plugin.getConfig().getDouble("explosive-minimum-damage", 5.0);
            double velocityMultiplier = plugin.getConfig().getDouble("explosive-velocity-multiplier", 1.1);
            int delay = plugin.getConfig().getInt("explosive-delay", 5);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!loc.getNearbyPlayers(radius).isEmpty()) {
                        handleExplosionBuildup(player, loc, radius, damage, minimumDamage, velocityMultiplier, delay);
                        plugin.getServer().getScheduler().runTaskLater(plugin, sc::remove, delay);
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 60, 2);
        });
    }

}
