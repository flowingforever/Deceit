package pro.fazeclan.river.deceit.util;

import org.bukkit.World;
import org.bukkit.entity.Player;
import pro.fazeclan.river.deceit.role.Faction;
import pro.fazeclan.river.deceit.role.Role;
import pro.fazeclan.river.jarona.game.GameValues;

import java.util.List;
import java.util.UUID;

public class RoleUtil {

    public static Role getRole(UUID uuid, GameValues values) {
        return values.getValue("role_" + uuid);
    }

    public static Role getRole(Player player, GameValues values) {
        return values.getValue("role_" + player.getUniqueId());
    }

    public static boolean areSameTeam(Player p1, Player p2, GameValues values) {
        var r1 = getRole(p1, values);
        var r2 = getRole(p2, values);
        return r1 != null && r2 != null && r1.isSameTeam(r2);
    }

    public static boolean canSeeTeam(Player viewer, Player target, GameValues values) {
        var r1 = getRole(viewer, values);
        var r2 = getRole(target, values);
        return (r1 != null && r2 != null
                && r1.isSameTeam(r2) && r1.canSeeTeam())
                || viewer.getGameMode().isInvulnerable()
                || viewer.equals(target);
    }

    public static boolean isEvil(Player player, GameValues values) {
        var role = getRole(player, values);
        return role != null && !getRole(player, values).getFaction().equals(Faction.INNOCENT);
    }

    public static boolean isInnocent(Player player, GameValues values) {
        var role = getRole(player, values);
        return role != null && getRole(player, values).getFaction().equals(Faction.INNOCENT);
    }

    public static boolean isTraitor(Player player, GameValues values) {
        var role = getRole(player, values);
        return role != null && role.getFaction().equals(Faction.TRAITOR);
    }

    public static boolean isTeamAlive(List<Player> players, GameValues values, Faction faction) {
        return players
                .stream()
                .filter(player -> !player.getGameMode().isInvulnerable())
                .anyMatch(player -> values.getValue("faction_" + player.getUniqueId(), Faction.INNOCENT).equals(faction));
    }

    public static boolean isTeamAlive(List<Player> players, GameValues values, String winsWith) {
        return players
                .stream()
                .filter(player -> !player.getGameMode().isInvulnerable())
                .filter(player -> RoleUtil.getRole(player, values) != null)
                .anyMatch(player -> RoleUtil.getRole(player, values).winsWith().equals(winsWith));
    }

    public static boolean onlyPlayersInFactionRemain(List<Player> players, GameValues values, Faction faction) {
        return players.stream()
                .filter(player -> !player.getGameMode().isInvulnerable())
                .allMatch(player -> values.getValue("faction_" + player.getUniqueId(), Faction.INNOCENT).equals(faction));
    }

    public static boolean onlyPlayersInTeamRemain(List<Player> players, GameValues values, String team) {
        return players.stream()
                .filter(player -> !player.getGameMode().isInvulnerable())
                .filter(player -> RoleUtil.getRole(player, values) != null)
                .allMatch(player -> RoleUtil.getRole(player, values).winsWith().equals(team));
    }

    public static List<Player> getAllInTeam(Player focus, List<Player> players, GameValues values) {
        return players
                .stream()
                .filter(p -> RoleUtil.areSameTeam(focus, p, values))
                .filter(p -> !p.getGameMode().isInvulnerable())
                .toList();
    }

    public static List<Player> getAllWithTeam(World world, GameValues values, String team) {
        return world.getPlayers()
                .stream()
                .filter(p -> !p.getGameMode().isInvulnerable())
                .filter(p -> {
                    var role = RoleUtil.getRole(p, values);
                    return role != null && role.winsWith().equals(team);
                })
                .toList();
    }

    public static List<Player> getAllWithRole(World world, GameValues values, String roleId) {
        return world.getPlayers()
                .stream()
                .filter(p -> !p.getGameMode().isInvulnerable())
                .filter(p -> {
                    var role = RoleUtil.getRole(p, values);
                    return role != null && role.getId().equals(roleId);
                })
                .toList();
    }

}
