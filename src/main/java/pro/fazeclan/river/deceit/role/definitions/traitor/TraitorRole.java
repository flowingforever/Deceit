package pro.fazeclan.river.deceit.role.definitions.traitor;

import net.kyori.adventure.text.minimessage.MiniMessage;
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

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.RED_DYE.createItemStack(meta -> {
            var mm = MiniMessage.miniMessage();
            meta.itemName(mm.deserialize(getName()));
            meta.lore(getDescription().stream().map(mm::deserialize).toList());
        });
    }

    @Override
    public String getPrefix() {
        return "<" + getMiniMessageColor() + ">\uD83E\uDE93</" + getMiniMessageColor() + ">";
    }
}
