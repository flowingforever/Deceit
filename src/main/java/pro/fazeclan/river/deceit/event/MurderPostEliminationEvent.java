package pro.fazeclan.river.deceit.event;

import lombok.Getter;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class MurderPostEliminationEvent extends Event {

    private final Player eliminated;
    private final Mannequin corpse;
    private final boolean revealed;

    public MurderPostEliminationEvent(Player eliminated, Mannequin corpse, boolean revealed) {
        this.eliminated = eliminated;
        this.corpse = corpse;
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
