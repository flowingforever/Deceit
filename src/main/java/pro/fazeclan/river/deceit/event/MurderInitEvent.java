package pro.fazeclan.river.deceit.event;

import lombok.Getter;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import pro.fazeclan.river.deceit.game.DeceitMurderGame;

import java.util.List;

@Getter
public class MurderInitEvent extends Event {

    private final List<Player> players;
    private final World world;
    private final DeceitMurderGame game;

    public MurderInitEvent(List<Player> players, World world, DeceitMurderGame game) {
        this.players = players;
        this.world = world;
        this.game = game;
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
