package pro.fazeclan.river.deceit.util;

import org.apache.commons.lang3.function.TriFunction;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pro.fazeclan.river.deceit.role.Role;
import pro.fazeclan.river.jarona.game.GameValues;
import pro.fazeclan.river.jarona.tablist.NameContext;

public class GameFunctions {

    public static void assignRole(Player player, Role role, GameValues values) {
        values.setValue("role_" + player.getUniqueId(), role);
        values.setValue("faction_" + player.getUniqueId(), role.getFaction());
        TriFunction<Player, Player, NameContext, String> name = values.setValue(
                "name_" + player.getUniqueId(),
                (t, v, ctx) -> {
                    if (v.getGameMode().isInvulnerable()
                            || (RoleUtil.canSeeTeam(t, v, values))) {
                        if (ctx.equals(NameContext.TABLIST)) {
                            return role.getPrefix() + " %jarona_nickname%";
                        } else {
                            return role.getPrefix() + " " + role.getName() + "<newline>%jarona_nickname%";
                        }
                    }

                    return "%jarona_nickname%";
                }
        );
        values.setValue("coins_" + player.getUniqueId(), role.getCoins());

        player.getInventory().clear();
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

    public static boolean buyIfPossible(Player player, GameValues values, int cost) {
        int coins = values.getValue("coins_" + player.getUniqueId(), 0);
        if (coins >= cost) {
            values.setValue("coins_" + player.getUniqueId(), coins - cost);
            return true;
        } else {
            return false;
        }
    }

}
