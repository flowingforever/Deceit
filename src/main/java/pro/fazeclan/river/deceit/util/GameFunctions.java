package pro.fazeclan.river.deceit.util;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.inventory.ItemStack;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.event.MurderPostEliminationEvent;
import pro.fazeclan.river.deceit.role.Role;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.game.GameValues;
import pro.fazeclan.river.jarona.tablist.NameContext;
import pro.fazeclan.river.jarona.util.GameUtil;
import pro.fazeclan.river.jarona.util.NametagUtil;
import pro.fazeclan.river.jarona.util.NicknameUtil;
import pro.fazeclan.river.jarona.util.QuadFunction;

import java.io.File;
import java.util.UUID;

public class GameFunctions {

    public static void assignRole(Player player, Role role, GameValues values) {
        values.setValue("role_" + player.getUniqueId(), role);
        values.setValue("faction_" + player.getUniqueId(), role.getFaction());
        NametagUtil.setName(player, values, (t, v, ctx, vl) -> {
            var builder = new StringBuilder();
            if (RoleUtil.canSeeTeam(v, t, values)
                    || vl.getValue("revealed_" + player.getUniqueId(), false)) {
                if (ctx.equals(NameContext.TABLIST)) {
                    builder.append(role.getPrefix()).append(" ");
                } else {
                    var color = role.getMiniMessageColor();
                    builder.append(role.getPrefix())
                            .append("<").append(color).append(">")
                            .append(role.getName())
                            .append("<newline>");
                }
            }

            builder.append("%jarona_nickname%");
            return builder.toString();
        });

        giveBells(player, values, role.getBells());

        player.getInventory().clear();
        player.setSaturation(2f);
        player.getInventory().setHeldItemSlot(0);
        for (ItemStack item : role.getSpawnItems()) {
            if (item == null) continue;
            player.give(item);
        }
    }

    public static void addPlayer(Player player, Role role, GameValues values, Location location) {

        assignRole(player, role, values);
        player.teleport(location);
        player.setGameMode(GameMode.ADVENTURE);

    }

    public static void eliminatePlayer(Player player, boolean revealed, boolean burning) {

        var game = GameUtil.getGame(player);
        if (game == null) {
            return;
        }
        var world = player.getWorld();
        var values = game.getGameValues(world.getUID());
        values.setValue("revealed_" + player.getUniqueId(), revealed);
        player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getValue());
        player.setGameMode(GameMode.SPECTATOR);

        // summon corpse
        var corpse = world.spawn(player.getLocation(), Mannequin.class, m -> {
            m.setProfile(ResolvableProfile.resolvableProfile(player.getPlayerProfile()));
            m.setCustomNameVisible(false);
            m.setDescription(null);
            m.setPose(Pose.SWIMMING, true);
            m.setInvulnerable(true);

            if (burning) {
                burnCorpse(Deceit.getInstance(), m, 2);
            }
        });

        // add time
        var config = YamlConfiguration.loadConfiguration(new File(world.getWorldFolder(), "map_config.yml"));
        values.setValue(
                "time_limit",
                values.getValue("time_limit", 0L) + config.getLong("deceit.added-time", 400)
        );

        // consider svc
        var svcPlugin = Jarona.getInstance().getVoicechatPlugin();
        if (svcPlugin != null) {
            svcPlugin.addSpectator(player);
        }

        Bukkit.getServer().getPluginManager().callEvent(new MurderPostEliminationEvent(player, corpse, revealed));

    }

    public static boolean buyIfPossible(Player player, GameValues values, int cost) {
        int coins = values.getValue("bells_" + player.getUniqueId(), 0);
        if (coins >= cost) {
            values.setValue("bells_" + player.getUniqueId(), coins - cost);
            return true;
        } else {
            return false;
        }
    }

    public static void payout(Player player, GameValues values) {
        giveBells(player, values, 2);
    }

    public static void giveBells(Player player, GameValues values, int bells) {
        values.setValue("bells_" + player.getUniqueId(), values.getValue("bells_" + player.getUniqueId(), 0) + bells);
    }

    public static void revealPlayerAsDead(UUID uuid, String name, GameValues values, World world) {
        values.setValue("revealed_" + uuid, true);
        var m = PacketEvents.getAPI().getPlayerManager();
        var p = new WrapperPlayServerPlayerInfoUpdate(
                WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_GAME_MODE,
                new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                        new UserProfile(uuid, name),
                        true,
                        0,
                        com.github.retrooper.packetevents.protocol.player.GameMode.SPECTATOR,
                        null,
                        null
                )
        );
        for (var player : world.getPlayers()) {
            m.sendPacket(player, p);
        }
    }

    public static void revealPlayerAsDead(Player player, GameValues values) {
        revealPlayerAsDead(player.getUniqueId(), player.getName(), values, player.getWorld());
    }

    public static void burnCorpse(Deceit plugin, Mannequin corpse, int seconds) {
        corpse.setVisualFire(TriState.TRUE);
        var world = corpse.getWorld();
        world.playSound(
                corpse.getLocation(),
                "minecraft:item.firecharge.use",
                1f,
                1f
        );

        plugin.getServer().getScheduler().runTaskLater(
                plugin,
                () -> {
                    if (!corpse.isValid()) {
                        return;
                    }

                    world.playSound(
                            corpse.getLocation(),
                            "minecraft:block.fire.extinguish",
                            1f,
                            1f
                    );
                    world.spawnParticle(
                            Particle.DUST,
                            corpse.getLocation(),
                            20,
                            1,
                            0.3,
                            1,
                            new Particle.DustOptions(Color.fromRGB(38, 18, 17), 1.5f)
                    );
                    corpse.remove();
                },
                seconds * 20L
        );
    }

}
