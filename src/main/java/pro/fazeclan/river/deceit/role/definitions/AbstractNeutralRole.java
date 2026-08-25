package pro.fazeclan.river.deceit.role.definitions;

import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.role.Faction;
import pro.fazeclan.river.deceit.role.Role;

public abstract class AbstractNeutralRole extends Role {

    public AbstractNeutralRole(Deceit plugin, String id) {
        super(plugin, id, Faction.NEUTRAL, true);
    }

    @Override
    public String winsWith() {
        return getId();
    }
}
