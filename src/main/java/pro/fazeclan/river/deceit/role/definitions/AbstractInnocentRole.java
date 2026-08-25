package pro.fazeclan.river.deceit.role.definitions;

import org.bukkit.entity.Player;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.role.Faction;
import pro.fazeclan.river.deceit.role.Role;
import pro.fazeclan.river.jarona.game.GameValues;

import java.util.List;

public abstract class AbstractInnocentRole extends Role {

    public AbstractInnocentRole(Deceit plugin, String id) {
        super(plugin, id, Faction.INNOCENT, false);
    }

    @Override
    public boolean hasWon(List<Player> players, GameValues values) {
        return players.stream()
                .filter(player -> !player.getGameMode().isInvulnerable())
                .allMatch(player -> values.getValue("faction_" + player.getUniqueId(), Faction.INNOCENT).equals(Faction.INNOCENT));
    }

    @Override
    public String winsWith() {
        return "innocent";
    }
}
