package pro.fazeclan.river.deceit.role.definitions;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.role.Faction;
import pro.fazeclan.river.deceit.role.Role;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.jarona.game.GameValues;

import java.util.List;

public abstract class AbstractInnocentRole extends Role {

    public AbstractInnocentRole(Deceit plugin, String id) {
        super(plugin, id, Faction.INNOCENT, false);
    }

    @Override
    public boolean hasWon(List<Player> players, GameValues values) {
        return RoleUtil.onlyPlayersInFactionRemain(players, values, getFaction())
                || values.getValue("time_limit", 0L) <= values.getValue("tick", 0L);
    }

    @Override
    public List<ItemStack> getSpawnItems() {
        return List.of(
                ItemType.BOW.createItemStack(meta -> meta.setUnbreakable(true)),
                ItemType.ARROW.createItemStack(20)
        );
    }

    @Override
    public String winsWith() {
        return "innocent";
    }
}
