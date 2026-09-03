package pro.fazeclan.river.deceit.modifier;

import lombok.Getter;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.modifier.definitions.LifelinkedModifier;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModifierManager {

    @Getter
    private final Map<String, Modifier> registry = new ConcurrentHashMap<>(16);
    private final Deceit plugin;

    public ModifierManager(Deceit plugin) {
        this.plugin = plugin;
    }

    public void registerAll() {
        register(new LifelinkedModifier(plugin));
    }

    public <R extends Modifier> R register(R modifier) {
        registry.put(modifier.getId(), modifier);
        return modifier;
    }

    public void reloadRegistry() {
        registry.clear();
        registerAll();
    }

    public Modifier getModifier(String id) {
        return registry.get(id);
    }

    public Collection<Modifier> getModifiers() {
        return registry.values();
    }

}
