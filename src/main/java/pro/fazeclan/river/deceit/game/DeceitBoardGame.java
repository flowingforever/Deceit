package pro.fazeclan.river.deceit.game;

import org.bukkit.World;
import org.bukkit.entity.Player;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.jarona.game.OverworldGame;

import java.util.List;

public class DeceitBoardGame extends OverworldGame {

    public DeceitBoardGame() {
        super(
                "Deceit: Board",
                Deceit.getKey("board"),
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
