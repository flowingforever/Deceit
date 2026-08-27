package pro.fazeclan.river.deceit;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import pro.fazeclan.river.deceit.ability.AbilityManager;
import pro.fazeclan.river.deceit.game.DeceitMurderGame;
import pro.fazeclan.river.deceit.listener.*;
import pro.fazeclan.river.deceit.role.RoleManager;
import pro.fazeclan.river.jarona.Jarona;

public final class Deceit extends JavaPlugin {

    @Getter
    private RoleManager roleManager;
    @Getter
    private AbilityManager abilityManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        var jarona = Jarona.getInstance();

        saveDefaultConfig();

        jarona.getGameManager().register(new DeceitMurderGame(this));

        this.roleManager = new RoleManager(this);
        roleManager.registerAll();
        this.abilityManager = new AbilityManager(this);
        abilityManager.registerAll();

        var pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new ShopListener(), this);
        pluginManager.registerEvents(new PreventionListener(), this);
        pluginManager.registerEvents(new CorpseListener(), this);
        pluginManager.registerEvents(new AbilityListener(this), this);

        var events = PacketEvents.getAPI().getEventManager();
        events.registerListener(new PreventionListener(), PacketListenerPriority.NORMAL);
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
