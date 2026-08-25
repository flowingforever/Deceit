package pro.fazeclan.river.deceit.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExhaustionEvent;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.util.GameFunctions;
import pro.fazeclan.river.jarona.util.GameUtil;

public class PreventionListener implements Listener, PacketListener {

    @EventHandler
    private void onExhaustion(EntityExhaustionEvent event) {
        if (GameUtil.hasGame(event.getEntity().getWorld(), Deceit.getKey("murder"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onKillingBlow(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }
        if (!GameUtil.hasGame(victim.getWorld(), Deceit.getKey("murder"))) {
            return;
        }
        if (event.getFinalDamage() < victim.getHealth()) {
            return;
        }
        event.setDamage(0.0);
        GameFunctions.eliminatePlayer(victim, true);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO_UPDATE) {
            var packet = new WrapperPlayServerPlayerInfoUpdate(event);
            if (!packet.getActions().contains(WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_GAME_MODE)) {
                return;
            }
            Player viewer = event.getPlayer();
            var entries = packet.getEntries();
            if (entries.stream().anyMatch(p -> p.getProfileId().equals(viewer.getUniqueId()))) {
                return;
            }
            if (!GameUtil.hasGame(viewer.getWorld(), Deceit.getKey("murder"))) {
                return;
            }
            var values = GameUtil.getGame(viewer).getGameValues(viewer.getWorld().getUID());
            if (entries.stream().noneMatch(p -> values.getValue("undiscovered_" + p.getProfileId(), false))) {
                return;
            }
            event.setCancelled(true);
        }
    }
}
