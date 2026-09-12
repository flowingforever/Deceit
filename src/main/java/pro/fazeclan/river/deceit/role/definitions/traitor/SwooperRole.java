package pro.fazeclan.river.deceit.role.definitions.traitor;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.role.definitions.AbstractTraitorRole;
import pro.fazeclan.river.deceit.util.MiscUtil;

import java.util.ArrayList;
import java.util.List;

public class SwooperRole extends AbstractTraitorRole {
    public SwooperRole(Deceit plugin) {
        super(plugin, "swooper");
    }

    @Override
    public ItemStack getDisplayItem() {
        return MiscUtil.createItem(Material.POTION, 1, stack -> {
            stack.editMeta(meta -> {
                meta.customName(Component.text("Swoop").decoration(TextDecoration.ITALIC, false));
                ((PotionMeta) meta).setBasePotionType(PotionType.INVISIBILITY);
            });
        });
    }

    @Override
    public List<ItemStack> getSpawnItems() {
        var list = new ArrayList<>(super.getSpawnItems());
        list.add(MiscUtil.createItem(Material.POTION, 1, stack -> {
            stack.editMeta(meta -> {
                meta.getPersistentDataContainer().set(
                        Deceit.getKey("ability"),
                        PersistentDataType.STRING,
                        "swoop"
                );
                meta.customName(Component.text("Swoop").decoration(TextDecoration.ITALIC, false));
                ((PotionMeta) meta).setBasePotionType(PotionType.INVISIBILITY);
            });
            stack.unsetData(DataComponentTypes.CONSUMABLE);
        }));
        return list;
    }
}
