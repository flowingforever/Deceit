package pro.fazeclan.river.deceit.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

public class MiscUtil {

    public static UUID fromStringOrNull(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }

        try {
            return UUID.fromString(s);
        } catch (IllegalArgumentException _) {
            return null;
        }
    }

    public static ItemStack createItem(Material material, int amount, Consumer<ItemStack> application) {
        var item = new ItemStack(material, amount);
        application.accept(item);
        return item;
    }

}
