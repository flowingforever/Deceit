package pro.fazeclan.river.deceit.menu.inventory;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public ItemStack getDisplayItem() {
        var stack = itemStacks.getFirst().clone();
        List<Component> lore;
        if (stack.lore() == null) {
            lore = new ArrayList<>();
        } else {
            lore = new ArrayList<>(Objects.requireNonNull(stack.lore()));
        }
        lore.add(Component.empty());
        lore.add(MiniMessage.miniMessage().deserialize(
                "<!i><dark_gray>Price: <yellow>" + cost + " \uD83D\uDD14"
        ));
        stack.lore(lore);
        return stack;
    }

}
