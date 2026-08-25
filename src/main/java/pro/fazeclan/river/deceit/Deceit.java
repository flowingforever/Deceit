package pro.fazeclan.river.deceit;

import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import pro.fazeclan.river.deceit.ability.AbilityManager;
import pro.fazeclan.river.deceit.game.DeceitMurderGame;
import pro.fazeclan.river.deceit.listener.ShopListener;
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

        getServer().getPluginManager().registerEvents(new ShopListener(), this);
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
