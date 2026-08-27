package pro.fazeclan.river.deceit.util;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pro.fazeclan.river.deceit.Deceit;

public class ExplosiveUtil {

    public static void handleExplosionBuildup(Deceit plugin, Player thrower, Location location, double radius, double damage, double minDmg, double veloMultiplier, int delay) {
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

    public static void handleExplosion(Player thrower, Location location, double radius, double damage, double minDmg, double veloMultiplier) {
        createExplosionEffect(location, radius / 2);
        damageNearbyPlayers(thrower, location, radius, damage, minDmg, veloMultiplier);
    }

    public static void createExplosionEffect(Location location, double offset) {
        location.getWorld().spawnParticle(
                Particle.EXPLOSION,
                location,
                15,
                offset,
                2,
                offset
        );

        location.getWorld().spawnParticle(
                Particle.LAVA,
                location,
                15,
                offset,
                0,
                offset
        );

        location.getWorld().playSound(
                location,
                Sound.ENTITY_GENERIC_EXPLODE,
                1.0F,
                0.7F
        );
    }

    public static void damageNearbyPlayers(Player thrower, Location location, double radius, double damage, double minDmg, double veloMultiplier) {
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

}
