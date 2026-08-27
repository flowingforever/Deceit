package pro.fazeclan.river.deceit.listener;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
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
            for (var entity : player.getWorld().getEntitiesByClass(Snowball.class)) {
                if (entity.getScoreboardTags().contains("grenade_" + player.getUniqueId())) {
                    var loc = entity.getLocation().clone();
                    entity.setGravity(false);
                    entity.setVelocity(new Vector());
                    handleExplosionBuildup(player, loc);
                    plugin.getServer().getScheduler().runTaskLater(plugin, entity::remove, plugin.getConfig().getInt("grenade-delay", 5));
                }
            }
            return;
        }
        player.setCooldown(item, plugin.getConfig().getInt("grenade-cooldown"));
        player.launchProjectile(Snowball.class, player.getLocation().getDirection().normalize(), s -> {
            s.getScoreboardTags().add("grenade_" + player.getUniqueId());
            s.setItem(new ItemStack(Material.CREEPER_HEAD));
        });
    }

    private void handleExplosionBuildup(Player thrower, Location location) {
        int delay = plugin.getConfig().getInt("grenade-delay", 5);
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
                handleExplosion(thrower, location);
                super.cancel();
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private void handleExplosion(Player thrower, Location location) {
        createExplosionEffect(location);
        damageNearbyPlayers(thrower, location);
    }

    private void createExplosionEffect(Location location) {
        location.getWorld().spawnParticle(
                Particle.EXPLOSION,
                location,
                1
        );

        location.getWorld().playSound(
                location,
                Sound.ENTITY_GENERIC_EXPLODE,
                1.0F,
                0.7F
        );
    }

    private void damageNearbyPlayers(Player thrower, Location location) {
        double radius = plugin.getConfig().getDouble("grenade-radius", 5.0);
        double damage = plugin.getConfig().getDouble("grenade-damage", 20.0);
        double velocityMultiplier = plugin.getConfig().getDouble("grenade-velocity-multiplier", 1.1);
        for (Player nearbyPlayer : location.getNearbyPlayers(radius)) {
            if (nearbyPlayer.getGameMode().isInvulnerable()) {
                continue;
            }

            var distance = location.distance(nearbyPlayer.getLocation());
            var distanceMultiplier = (radius - distance) * velocityMultiplier;
            var push = location.toVector().subtract(nearbyPlayer.getLocation().toVector()).multiply(-1);
            if (push.lengthSquared() > 0) {
                push.normalize();
            } else {
                push = new Vector(0, 1, 0);
            }
            push.add(new Vector(0, 0.2, 0));

            push.multiply(distanceMultiplier);

            nearbyPlayer.damage(damage, thrower);
            nearbyPlayer.setVelocity(push);
        }
    }

    @EventHandler
    private void onGrenadeWallHit(ProjectileHitEvent event) {
        var tags = event.getEntity().getScoreboardTags();
        if (tags.stream().noneMatch(tag -> tag.contains("grenade_"))) {
            return;
        }
        var s = event.getEntity();

        if (event.getHitBlockFace() != null) {
            var velo = s.getVelocity();
            var loc = s.getLocation();
            var hitFace = event.getHitBlockFace();

            velo.multiply(hitFace.getDirection().multiply(0.9));
            loc.add(hitFace.getDirection().multiply(0.25));

            var world = s.getWorld();
            world.spawn(loc, Snowball.class, sn -> {
                for (var t : s.getScoreboardTags()) {
                    sn.getScoreboardTags().add(t);
                }
                sn.setItem(new ItemStack(Material.CREEPER_HEAD));
                sn.setVelocity(velo);
                sn.setShooter(s.getShooter());

                if (s.getVelocity().length() < plugin.getConfig().getDouble("grenade-leniency", 0.15)) {
                    sn.setGravity(false);
                    sn.setVelocity(new Vector());
                    handleExplosionBuildup((Player) sn.getShooter(), sn.getLocation());
                    plugin.getServer().getScheduler().runTaskLater(plugin, sn::remove, plugin.getConfig().getInt("grenade-delay", 5));
                }
            });

        }

        event.setCancelled(true);
    }


}
