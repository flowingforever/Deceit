package pro.fazeclan.river.deceit.role.definitions;

import org.bukkit.entity.Player;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.role.Faction;
import pro.fazeclan.river.deceit.role.Role;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.jarona.game.GameValues;

import java.util.List;

public abstract class AbstractTraitorRole extends Role {

    public AbstractTraitorRole(Deceit plugin, String id) {
        super(plugin, id, Faction.TRAITOR, false);
    }

    @Override
    public boolean hasWon(List<Player> players, GameValues values) {
        return !RoleUtil.isTeamAlive(players, values, Faction.INNOCENT)
                && !RoleUtil.isTeamAlive(players, values, Faction.NEUTRAL)
                && values.getValue("time_limit", 0L) > values.getValue("tick", 0L);
    }

    @Override
    public String winsWith() {
        return "traitor";
    }

}
