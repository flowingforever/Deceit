package pro.fazeclan.river.deceit.ability;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AbilityEvent extends PlayerEvent {

    @Getter
    private final String expectedAbility;
    @Getter
    private final ItemStack itemStack;

    public AbilityEvent(@NotNull Player player, ItemStack itemStack, String expectedAbility) {
        super(player);
        this.expectedAbility = expectedAbility;
        this.itemStack = itemStack;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    private static HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
