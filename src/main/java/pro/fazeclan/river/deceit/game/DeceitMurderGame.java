package pro.fazeclan.river.deceit.game;

import org.bukkit.World;
import org.bukkit.entity.Player;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.jarona.game.GameWithMap;

import java.util.List;

// say that again...
public class DeceitMurderGame extends GameWithMap {

    public DeceitMurderGame() {
        super(
                "Deceit: Murder",
                Deceit.getKey("murder"),
                2
        );
    }

    @Override
    public void init(World world, List<Player> list) {

    }

    @Override
    public void tick(World world, List<Player> list) {

    }

    @Override
    public void end(World world, List<Player> list) {

    }

}
