package pro.fazeclan.river.deceit.role.definitions.innocent;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.persistence.PersistentDataType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.role.definitions.AbstractInnocentRole;

import java.util.List;

public class SwapperRole extends AbstractInnocentRole {

    public SwapperRole(Deceit plugin) {
        super(plugin, "swapper");
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.DISPENSER.createItemStack();
    }

    @Override
    public String getPrefix() {
        return "<" + getMiniMessageColor() + ">◆</" + getMiniMessageColor() + ">";
    }

    @Override
    public List<ItemStack> getSpawnItems() {
        return List.of(
                ItemType.BOW.createItemStack(meta -> meta.setUnbreakable(true)),
                ItemType.ARROW.createItemStack(20),
                ItemType.ENDER_EYE.createItemStack(meta -> {
                    meta.getPersistentDataContainer().set(
                            Deceit.getKey("ability"),
                            PersistentDataType.STRING,
                            "swapper"
                    );
                    meta.itemName(Component.text("Swapper"));
                })
        );
    }

}
