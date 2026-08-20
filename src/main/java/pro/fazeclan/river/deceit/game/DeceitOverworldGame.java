package pro.fazeclan.river.deceit.game;

import org.bukkit.World;
import org.bukkit.entity.Player;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.jarona.game.OverworldGame;

import java.util.List;

public class DeceitOverworldGame extends OverworldGame {

    public DeceitOverworldGame() {
        super(
                "Deceit: Overworld",
                Deceit.getKey("overworld"),
                6
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
