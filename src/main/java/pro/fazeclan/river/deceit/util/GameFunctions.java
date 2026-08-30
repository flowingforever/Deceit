package pro.fazeclan.river.deceit.util;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.inventory.ItemStack;
import pro.fazeclan.river.deceit.role.Role;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.game.GameValues;
import pro.fazeclan.river.jarona.tablist.NameContext;
import pro.fazeclan.river.jarona.util.GameUtil;
import pro.fazeclan.river.jarona.util.QuadFunction;

import java.io.File;
import java.util.UUID;

public class GameFunctions {

    public static void assignRole(Player player, Role role, GameValues values) {
        values.setValue("role_" + player.getUniqueId(), role);
        values.setValue("faction_" + player.getUniqueId(), role.getFaction());
        values.setValue(
                "name_" + player.getUniqueId(),
                (QuadFunction<Player, Player, NameContext, GameValues, String>) (t, v, ctx, vl) -> {
                    if (RoleUtil.canSeeTeam(v, t, values)
                            || vl.getValue("revealed_" + player.getUniqueId(), false)) {
                        if (ctx.equals(NameContext.TABLIST)) {
                            return role.getPrefix() + " %jarona_nickname%";
                        } else {
                            return role.getPrefix() + " " + role.getName() + "<newline>%jarona_nickname%";
                        }
                    }

                    return "%jarona_nickname%";
                }
        );
        giveBells(player, values, role.getBells());

        player.getInventory().clear();
        player.setSaturation(2f);
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

    public static void eliminatePlayer(Player player, boolean revealed) {

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
        world.spawn(player.getLocation(), Mannequin.class, m -> {
            m.setProfile(ResolvableProfile.resolvableProfile(player.getPlayerProfile()));
            m.setCustomNameVisible(false);
            m.setDescription(null);
            m.setPose(Pose.SWIMMING, true);
            m.setInvulnerable(true);
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

}
