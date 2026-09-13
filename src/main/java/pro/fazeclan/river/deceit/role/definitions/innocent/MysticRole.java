package pro.fazeclan.river.deceit.role.definitions.innocent;

import org.bukkit.event.EventHandler;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.event.MurderPostEliminationEvent;
import pro.fazeclan.river.deceit.role.definitions.AbstractInnocentRole;
import pro.fazeclan.river.deceit.util.GlowUtil;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.jarona.util.GameUtil;

public class MysticRole extends AbstractInnocentRole {

    public MysticRole(Deceit plugin) {
        super(plugin, "mystic");
    }

    @EventHandler
    private void mysticPlayerElimination(MurderPostEliminationEvent event) {
        var corpse = event.getCorpse();
        var world = corpse.getWorld();
        var values = GameUtil.getGame(world).getGameValues(world.getUID());
        var mystics = corpse.getWorld().getPlayers()
                .stream()
                .filter(p -> !p.getGameMode().isInvulnerable())
                .filter(p -> RoleUtil.getRole(p, values) != null)
                .filter(p -> RoleUtil.getRole(p, values).getId().equalsIgnoreCase(getId()))
                .toList();

        var scheduler = getPlugin().getServer().getScheduler();
        for (var mystic : mystics) {
            GlowUtil.setGlowing(corpse, mystic, true);

            scheduler.runTaskLater(getPlugin(), () -> {
                if (corpse.isValid() && mystic.isConnected()) {
                    GlowUtil.setGlowing(corpse, mystic, false);
                }
            }, getProperty("duration", 3) * 20);
        }
    }

}
