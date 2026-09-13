package pro.fazeclan.river.deceit.role.definitions.traitor;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.role.definitions.AbstractTraitorRole;

import java.util.List;

public class TraitorRole extends AbstractTraitorRole {
    public TraitorRole(Deceit plugin) {
        super(plugin, "traitor");
    }

    @Override
    public List<ItemStack> getSpawnItems() {
        return List.of(
                ItemType.BOW.createItemStack(meta -> meta.setUnbreakable(true)),
                ItemType.ARROW.createItemStack(40)
        );
    }

}
