package pro.fazeclan.river.deceit.ability;

import lombok.Getter;
import org.bukkit.event.HandlerList;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.ability.definitions.BackstabAbility;
import pro.fazeclan.river.deceit.ability.definitions.CreepanadeAbility;
import pro.fazeclan.river.deceit.ability.definitions.ExplosiveAbility;
import pro.fazeclan.river.deceit.ability.definitions.TrackerAbility;

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
        register(new CreepanadeAbility(plugin));
        register(new ExplosiveAbility(plugin));
        register(new TrackerAbility(plugin));
        register(new BackstabAbility(plugin));
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
