package pro.fazeclan.river.deceit.modifier;

import lombok.Getter;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.jarona.game.Game;
import pro.fazeclan.river.jarona.game.GameValues;

import java.io.File;
import java.util.List;

public abstract class Modifier implements Listener {

    private final File file;
    @Getter
    private final YamlConfiguration config;

    @Getter
    private final String id;

    private final Deceit plugin;

    public Modifier(Deceit plugin, String id) {
        this.id = id;
        this.plugin = plugin;

        plugin.saveResource("modifiers/" + id + ".yml", false);
        this.file = new File(plugin.getDataFolder(), "modifiers/" + id + ".yml");
        this.config = YamlConfiguration.loadConfiguration(this.file);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public <T> T getProperty(String key, T def) {
        return (T) config.get(key, def);
    }

    public abstract void init(List<Player> players, World world, GameValues values);
    public abstract void tick(List<Player> players, World world, GameValues values);

    public void reload() {
        try {
            config.load(file);
        } catch (Exception _) {
            plugin.getLogger().warning("Modifier " + getId() + " failed to reload!");
        }
    }

    public void reset() {
        plugin.saveResource("modifiers/" + id + ".yml", true);
        reload();
    }

    public void save() {
        try {
            config.save(file);
        } catch (Exception _) {
            plugin.getLogger().warning("Modifier " + getId() + " failed to save!");
        }
    }

}
