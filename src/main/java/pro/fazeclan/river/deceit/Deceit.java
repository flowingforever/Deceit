package pro.fazeclan.river.deceit;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import pro.fazeclan.river.deceit.ability.AbilityManager;
import pro.fazeclan.river.deceit.command.ConfigCommand;
import pro.fazeclan.river.deceit.command.TeamChatCommand;
import pro.fazeclan.river.deceit.game.DeceitMurderGame;
import pro.fazeclan.river.deceit.listener.AbilityListener;
import pro.fazeclan.river.deceit.listener.CorpseListener;
import pro.fazeclan.river.deceit.listener.PreventionListener;
import pro.fazeclan.river.deceit.listener.ShopListener;
import pro.fazeclan.river.deceit.role.RoleManager;
import pro.fazeclan.river.jarona.Jarona;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class Deceit extends JavaPlugin {

    @Getter
    private RoleManager roleManager;
    @Getter
    private AbilityManager abilityManager;

    @Override
    public void onEnable() {
        var jarona = Jarona.getInstance();

        // config
        saveDefaultConfig();

        // register game
        jarona.getGameManager().register(new DeceitMurderGame(this));

        // managers
        this.roleManager = new RoleManager(this);
        roleManager.registerAll();
        this.abilityManager = new AbilityManager(this);
        abilityManager.registerAll();

        // listeners
        var pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new ShopListener(), this);
        pluginManager.registerEvents(new PreventionListener(), this);
        pluginManager.registerEvents(new CorpseListener(), this);
        pluginManager.registerEvents(new AbilityListener(this), this);

        var events = PacketEvents.getAPI().getEventManager();
        events.registerListener(new PreventionListener(), PacketListenerPriority.NORMAL);

        // commands
        List<Pair<LiteralArgumentBuilder<CommandSourceStack>, Collection<String>>> subcommands = new ArrayList<>();
        var command = Commands.literal("deceit");
        subcommands.add(ConfigCommand.command(this));
        subcommands.add(TeamChatCommand.command());

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            // add each subcommand and register them
            subcommands.forEach(subcommand -> {
                command.then(subcommand.getLeft());
                commands.registrar().register(subcommand.getLeft().build(), subcommand.getRight());
            });

            // root command
            commands.registrar().register(command.build());
        });
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
