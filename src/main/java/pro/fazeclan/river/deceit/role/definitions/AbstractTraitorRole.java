package pro.fazeclan.river.deceit.role.definitions;

import org.bukkit.entity.Player;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.role.Faction;
import pro.fazeclan.river.deceit.role.Role;
import pro.fazeclan.river.jarona.game.GameValues;

import java.util.List;

public abstract class AbstractTraitorRole extends Role {

    public AbstractTraitorRole(Deceit plugin, String id) {
        super(plugin, id, Faction.TRAITOR, false);
    }

    @Override
    public boolean hasWon(List<Player> players, GameValues values) {
        return players.stream()
                .filter(player -> !player.getGameMode().isInvulnerable())
                .allMatch(player -> values.getValue("faction_" + player.getUniqueId(), Faction.INNOCENT).equals(Faction.TRAITOR));
    }

    @Override
    public String winsWith() {
        return "traitor";
    }

}
