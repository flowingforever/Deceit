package pro.fazeclan.river.deceit.role;

import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.menu.ShopEntry;
import pro.fazeclan.river.jarona.game.GameValues;

import java.io.File;
import java.util.List;

public abstract class Role implements Listener {

    private final File file;
    @Getter
    private final YamlConfiguration config;

    @Getter
    private final String id;

    @Getter
    private final Faction faction;
    @Getter
    private final boolean takesPriority; // more for the neutral roles
    @Getter
    private final Deceit plugin;

    public Role(Deceit plugin, String id, Faction faction, boolean takesPriority) {
        this.plugin = plugin;
        plugin.saveResource("roles/" + id + ".yml", false);
        this.file = new File(plugin.getDataFolder(), "roles/" + id + ".yml");
        this.config = YamlConfiguration.loadConfiguration(this.file);

        this.id = id;
        this.faction = faction;
        this.takesPriority = takesPriority;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public <T> T getProperty(String key, T def) {
        return (T) config.get(key, def);
    }

    public abstract List<ItemStack> getSpawnItems();
    public abstract List<ShopEntry> getShopItems();
    public abstract ItemStack getDisplayItem();
    public abstract boolean hasWon(List<Player> players, GameValues values);
    public abstract String getPrefix();
    public abstract String winsWith();

    public String getName() {
        return getProperty("name", "None");
    }

    public boolean isEnabled() {
        return getProperty("enabled", true);
    }

    public List<String> getDescription() {
        return getProperty("description", List.of());
    }

    public boolean winningEndsGames() {
        return getProperty("winning-ends-games", true);
    }

    public int getBells() {
        return getProperty("bells", 0);
    }

    public String getAnnouncement() {
        return getProperty("announcement", getName());
    }

    public boolean canSeeTeam() {
        return getProperty("see-own-team", false);
    }

    public boolean needsSelection() {
        return !getProperty("selection.remainder", false);
    }

    public double getSelectionPercentage() {
        if (getProperty("selection.remainder", false)) {
            return 100.0;
        } else {
            return getProperty("selection.percentage", 100.0);
        }
    }

    public int getMinimumCount() {
        if (getProperty("selection.remainder", false)) {
            return 0;
        } else {
            return getProperty("selection.minimum", 1);
        }
    }

    public Sound getAnnouncementSound() {
        String sound = getProperty("announcement-sound", "minecraft:block.note_block.bell");
        float pitch = getProperty("announcement-pitch", 1.0).floatValue();
        return Sound.sound(
                Key.key(sound),
                Sound.Source.PLAYER,
                1.0f,
                pitch
        );
    }

    public String getMiniMessageColor() {
        return getProperty("color", "green");
    }

    public boolean isSameTeam(Role other) {
        if (faction == Faction.NEUTRAL) {
            return other.equals(this);
        } else {
            return other.faction.equals(faction);
        }
    }

    public void reloadRole() {
        try {
            config.load(file);
        } catch (Exception _) {
            plugin.getLogger().warning("Role " + getId() + " failed to reload!");
        }
    }

    public void resetRole() {
        plugin.saveResource("roles/" + id + ".yml", true);
        reloadRole();
    }

    public void saveRole() {
        try {
            config.save(file);
        } catch (Exception _) {
            plugin.getLogger().warning("Role " + getId() + " failed to save!");
        }
    }

}
