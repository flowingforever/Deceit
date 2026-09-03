package pro.fazeclan.river.deceit.event;

import lombok.Getter;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class MurderPreEliminationEvent extends Event {

    private final Player eliminated;
    private final DamageSource source;
    private final boolean revealed;

    public MurderPreEliminationEvent(Player eliminated, DamageSource source, boolean revealed) {
        this.eliminated = eliminated;
        this.source = source;
        this.revealed = revealed;
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
