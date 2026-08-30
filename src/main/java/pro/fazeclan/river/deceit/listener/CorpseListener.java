package pro.fazeclan.river.deceit.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.TriState;
import org.bukkit.entity.Display;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.util.GameFunctions;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.jarona.util.GameUtil;

public class CorpseListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    private void onCorpseInteract(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof Mannequin corpse)) {
            return;
        }
        var world = corpse.getWorld();
        if (!GameUtil.hasGame(world, Deceit.getKey("murder"))) return;
        if (corpse.getVisualFire().equals(TriState.TRUE)) return;
        if (!corpse.getPassengers().isEmpty()) return;
        if (event.getPlayer().getGameMode().isInvulnerable()) return;
        var values = GameUtil.getGame(world).getGameValues(world.getUID());
        var profile = corpse.getProfile();
        if (profile.name() != null && profile.uuid() != null) {
            GameFunctions.revealPlayerAsDead(profile.uuid(), profile.name(), values, corpse.getWorld());
            var loc = corpse.getLocation().clone();
            loc.setPitch(0f);
            world.spawn(loc, TextDisplay.class, td -> {
                td.text(Component.text(profile.name())
                        .append(Component.newline())
                        .append(MiniMessage.miniMessage().deserialize(RoleUtil.getRole(profile.uuid(), values).getName())));
                td.setTransformation(new Transformation(
                        new Vector3f(0f, 0.5f, 0f),
                        new Quaternionf(),
                        new Vector3f(0.5f, 0.5f, 0.5f),
                        new Quaternionf()
                ));
                td.setBillboard(Display.Billboard.VERTICAL);
                corpse.addPassenger(td);
            });
        }
    }

}
