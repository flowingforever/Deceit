package pro.fazeclan.river.deceit.role;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pro.fazeclan.river.deceit.Deceit;

import java.io.File;
import java.util.List;

public abstract class Role {

    @Getter
    private final String id;

    private final File file;
    private final YamlConfiguration config;

    public Role(Deceit plugin, String id) {
        plugin.saveResource("roles/" + id + ".yml", false);
        this.file = new File(plugin.getDataFolder(), "roles/" + id + ".yml");
        this.config = YamlConfiguration.loadConfiguration(this.file);

        this.id = id;
    }

    public <T> T getProperty(String key, T def) {
        return (T) config.get(key, def);
    }

    public abstract int getMaxPlayers(List<Player> players);
    public abstract ItemStack[] getSpawnItems();
    public abstract ItemStack[] getShopItems();

}
