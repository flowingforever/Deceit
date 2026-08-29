package pro.fazeclan.river.deceit.role;

import lombok.Getter;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.role.definitions.innocent.InnocentRole;
import pro.fazeclan.river.deceit.role.definitions.innocent.SheriffRole;
import pro.fazeclan.river.deceit.role.definitions.traitor.TraitorRole;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoleManager {

    @Getter
    private final Map<String, Role> registry = new ConcurrentHashMap<>(16);
    private final Deceit plugin;

    public RoleManager(Deceit plugin) {
        this.plugin = plugin;
    }

    public void registerAll() {
        // innocent
        register(new InnocentRole(plugin));
        register(new SheriffRole(plugin));

        // neutral

        // traitor
        register(new TraitorRole(plugin));
    }

    public <R extends Role> R register(R role) {
        registry.put(role.getId(), role);
        return role;
    }

    public void reloadRegistry() {
        registry.clear();
        registerAll();
    }

    public Role getRole(String id) {
        return registry.get(id);
    }

    public Collection<Role> getRoles() {
        return registry.values();
    }

    public List<Role> getRoles(Faction faction) {
        return getRoles()
                .stream()
                .filter(Role::isEnabled)
                .filter(role -> role.getFaction().equals(faction))
                .toList();
    }

    public List<Role> getLimitedRoles() {
        return getRoles()
                .stream()
                .filter(Role::isEnabled)
                .filter(Role::needsSelection)
                .toList();
    }

    public List<Role> getLimitedRoles(Faction faction) {
        return getRoles()
                .stream()
                .filter(Role::isEnabled)
                .filter(role -> role.getFaction().equals(faction))
                .filter(Role::needsSelection)
                .toList();
    }

    public List<Role> getUnlimitedRoles() {
        return getRoles()
                .stream()
                .filter(Role::isEnabled)
                .filter(role -> !role.needsSelection())
                .toList();
    }

    public List<Role> getUnlimitedRoles(Faction faction) {
        return getRoles()
                .stream()
                .filter(Role::isEnabled)
                .filter(role -> role.getFaction().equals(faction))
                .filter(role -> !role.needsSelection())
                .toList();
    }

}
