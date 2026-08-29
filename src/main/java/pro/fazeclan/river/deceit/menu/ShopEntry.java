package pro.fazeclan.river.deceit.menu;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShopEntry {

    @Getter
    private final List<ItemStack> itemStacks;
    @Getter
    private final int cost;

    public ShopEntry(ItemStack itemStack, int cost) {
        this.itemStacks = List.of(itemStack);
        this.cost = cost;
    }

    public ShopEntry(List<ItemStack> itemStacks, int cost) {
        this.itemStacks = itemStacks;
        this.cost = cost;
    }

}
