package pro.fazeclan.river.deceit.role.definitions.innocent;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.role.definitions.AbstractInnocentRole;

public class InnocentRole extends AbstractInnocentRole {

    public InnocentRole(Deceit plugin) {
        super(plugin, "innocent");
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.GREEN_DYE.createItemStack(meta -> {
            var mm = MiniMessage.miniMessage();
            meta.itemName(mm.deserialize(getName()));
            meta.lore(getDescription().stream().map(mm::deserialize).toList());
        });
    }

    @Override
    public String getPrefix() {
        return "<" + getMiniMessageColor() + ">◆</" + getMiniMessageColor() + ">";
    }
}
