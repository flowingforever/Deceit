package pro.fazeclan.river.deceit.util;

import org.bukkit.entity.Player;
import pro.fazeclan.river.deceit.role.Faction;
import pro.fazeclan.river.deceit.role.Role;
import pro.fazeclan.river.jarona.game.GameValues;

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
        return r1.isSameTeam(r2);
    }

    public static boolean canSeeTeam(Player p1, Player p2, GameValues values) {
        var r1 = getRole(p1, values);
        var r2 = getRole(p2, values);
        return r1.isSameTeam(r2) && r1.getFaction() != Faction.INNOCENT;
    }

}
