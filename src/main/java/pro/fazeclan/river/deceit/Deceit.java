package pro.fazeclan.river.deceit;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import pro.fazeclan.river.deceit.game.DeceitBoardGame;
import pro.fazeclan.river.deceit.game.DeceitMurderGame;
import pro.fazeclan.river.jarona.Jarona;

public final class Deceit extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        var jarona = Jarona.getInstance();

        jarona.getGameManager().register(new DeceitMurderGame());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Deceit getInstance() {
        return JavaPlugin.getPlugin(Deceit.class);
    }

    public static NamespacedKey getKey(String value) {
        return new NamespacedKey("deceit", value);
    }

}
