package pro.fazeclan.river.deceit.ability;

import lombok.Getter;
import org.bukkit.event.HandlerList;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.ability.definitions.innocent.*;
import pro.fazeclan.river.deceit.ability.definitions.traitor.*;
import pro.fazeclan.river.deceit.ability.definitions.neutral.DouseAbility;
import pro.fazeclan.river.deceit.ability.definitions.neutral.FakeDaggerAbility;

import java.util.HashMap;
import java.util.Map;

public class AbilityManager {

    @Getter
    private final Map<String, Ability> registry = new HashMap<>();
    private final Deceit plugin;

    public AbilityManager(Deceit plugin) {
        this.plugin = plugin;
    }

    public void registerAll() {
        // traitor
        register(new CreepanadeAbility(plugin));
        register(new ExplosiveAbility(plugin));
        register(new TrackerAbility(plugin));
        register(new BackstabAbility(plugin));
        register(new FakeHealthKitAbility(plugin));
        register(new TorchAbility(plugin));
        register(new TeleporterAbility(plugin));
        register(new RevivalAbility(plugin));
        register(new SwoopAbility(plugin));
        register(new MuzzleAbility(plugin));

        // neutral
        register(new FakeDaggerAbility(plugin));
        register(new DouseAbility(plugin));

        // innocent
        register(new SheriffCapAbility(plugin));
        register(new ScannerAbility(plugin));
        register(new HealthKitAbility(plugin));
        register(new ScalpelAbility(plugin));
        register(new SwapperAbility(plugin));
    }

    public void reloadRegistry() {
        for (Map.Entry<String, Ability> entry : registry.entrySet()) {
            HandlerList.unregisterAll(entry.getValue());
        }
        registry.clear();
        registerAll();
    }

    public <T extends Ability> T register(T ability) {
        registry.put(ability.getId(), ability);
        return ability;
    }

    public void unregister(Ability ability) {
        registry.remove(ability.getId());
        HandlerList.unregisterAll(ability);
    }

}
