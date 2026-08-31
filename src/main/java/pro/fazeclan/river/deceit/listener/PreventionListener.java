package pro.fazeclan.river.deceit.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExhaustionEvent;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.util.GameFunctions;
import pro.fazeclan.river.jarona.util.GameUtil;

import java.util.ArrayList;

public class PreventionListener implements Listener, PacketListener {

    @EventHandler
    private void onExhaustion(EntityExhaustionEvent event) {
        if (GameUtil.hasGame(event.getEntity().getWorld(), Deceit.getKey("murder"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onKillingAnythingElseBlow(EntityDamageEvent event) {
        eliminatePlayer(event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onKillingBlockBlow(EntityDamageByBlockEvent event) {
        eliminatePlayer(event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onKillingBlow(EntityDamageByEntityEvent event) {
        eliminatePlayer(event);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO_UPDATE) {
            var packet = new WrapperPlayServerPlayerInfoUpdate(event);
            if (!packet.getActions().contains(WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_GAME_MODE)) return;
            Player viewer = event.getPlayer();
            if (!GameUtil.hasGame(viewer.getWorld(), Deceit.getKey("murder"))) return;
            if (viewer.getGameMode().isInvulnerable()) return;
            var values = GameUtil.getGame(viewer).getGameValues(viewer.getWorld().getUID());
            var entries = new ArrayList<>(packet.getEntries());
            for (var entry : packet.getEntries()) {
                if (entry.getProfileId().equals(viewer.getUniqueId())) continue;
                if (values.getValue("revealed_" + entry.getProfileId(), true)) continue;
                entry.setGameMode(GameMode.ADVENTURE);
            }
            packet.setEntries(entries);
            event.markForReEncode(true);
        }
    }

    private void eliminatePlayer(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!GameUtil.hasGame(victim.getWorld(), Deceit.getKey("murder"))) return;
        if (event.getFinalDamage() < victim.getHealth()) return;

        event.setDamage(0.0);
        boolean discovered = false;
        if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID)
                || event.getCause().equals(EntityDamageEvent.DamageCause.KILL)) {
            victim.teleport(victim.getWorld().getSpawnLocation().clone().add(0, 20, 0));
            discovered = true;
        }
        GameFunctions.eliminatePlayer(victim, discovered);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onSpectatorChat(AsyncChatEvent event) {
        if (!GameUtil.hasGame(event.getPlayer().getWorld(), Deceit.getKey("murder"))) return;
        if (!event.getPlayer().getGameMode().isInvulnerable()) return;
        event.viewers().removeIf(viewer -> viewer instanceof Player player && !player.getGameMode().isInvulnerable());
    }

}
