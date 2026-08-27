package pro.fazeclan.river.deceit.ability;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.jarona.condition.*;

import java.io.File;

public abstract class Ability implements Listener {

    @Getter
    private final String id;

    private final File file;
    private final YamlConfiguration config;
    @Getter
    private final Deceit plugin;

    public Ability(Deceit plugin, String id) {
        plugin.saveResource("abilities/" + id + ".yml", false);
        this.file = new File(plugin.getDataFolder(), "abilities/" + id + ".yml");
        this.config = YamlConfiguration.loadConfiguration(this.file);

        this.id = id;

        this.plugin = plugin;
        plugin.getServer()
                .getPluginManager()
                .registerEvents(this, plugin);
    }

    public <T> T getProperty(String key, T def) {
        return (T) config.get(key, def);
    }

    public String buildUses(int max, int used) {
        var sb = new StringBuilder();
        for (int i = 0; i < max; i++) {
            if (i < max - used) {
                sb.append("<green>■</green>").append(" ");
            } else {
                sb.append("<red>■</red>").append(" ");
            }
        }
        return sb.toString().trim();
    }

    public boolean hasAbility(ItemStack stack) {
        return stack.getPersistentDataContainer().has(Deceit.getKey("ability"))
                && stack.getPersistentDataContainer().get(Deceit.getKey("ability"), PersistentDataType.STRING).equals(getId());
    }

    public abstract ItemStack getDisplayItem();

}
