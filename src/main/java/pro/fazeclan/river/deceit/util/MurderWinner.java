package pro.fazeclan.river.deceit.util;

import org.bukkit.entity.Player;
import pro.fazeclan.river.jarona.game.GameValues;

import java.util.List;

public interface MurderWinner {

    String getPrefix();
    String getMiniMessageColor();
    String getName();
    boolean hasWon(List<Player> players, GameValues values);
    String winsWith();
    boolean winningEndsGames();

}
