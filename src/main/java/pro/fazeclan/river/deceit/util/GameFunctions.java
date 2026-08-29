package pro.fazeclan.river.deceit.util;

import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.function.TriFunction;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.inventory.ItemStack;
import pro.fazeclan.river.deceit.role.Role;
import pro.fazeclan.river.jarona.game.GameValues;
import pro.fazeclan.river.jarona.tablist.NameContext;
import pro.fazeclan.river.jarona.util.GameUtil;
import pro.fazeclan.river.jarona.util.QuadFunction;

import java.io.File;

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
        giveCoins(player, values, role.getCoins());

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

    public static void eliminatePlayer(Player player, boolean undiscovered) {

        var game = GameUtil.getGame(player);
        if (game == null) {
            return;
        }
        var world = player.getWorld();
        var values = game.getGameValues(world.getUID());
        values.setValue("undiscovered_" + player.getUniqueId(), undiscovered);
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

        // todo: consider svc

    }

    public static boolean buyIfPossible(Player player, GameValues values, int cost) {
        int coins = values.getValue("coins_" + player.getUniqueId(), 0);
        if (coins >= cost) {
            values.setValue("coins_" + player.getUniqueId(), coins - cost);
            return true;
        } else {
            return false;
        }
    }

    public static void payout(Player player, GameValues values) {
        giveCoins(player, values, 2);
    }

    public static void giveCoins(Player player, GameValues values, int coins) {
        values.setValue("coins_" + player.getUniqueId(), values.getValue("coins_" + player.getUniqueId(), 0) + coins);
    }

}
