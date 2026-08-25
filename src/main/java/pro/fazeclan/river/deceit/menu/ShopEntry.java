package pro.fazeclan.river.deceit.menu;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public class ShopEntry {

    @Getter
    private final ItemStack itemStack;
    @Getter
    private final int cost;

    public ShopEntry(ItemStack itemStack, int cost) {
        this.itemStack = itemStack;
        this.cost = cost;
    }

}
