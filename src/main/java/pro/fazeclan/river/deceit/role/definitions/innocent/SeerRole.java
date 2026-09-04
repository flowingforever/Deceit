package pro.fazeclan.river.deceit.role.definitions.innocent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.event.MurderEndEvent;
import pro.fazeclan.river.deceit.role.definitions.AbstractInnocentRole;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.jarona.condition.TimedCondition;
import pro.fazeclan.river.jarona.util.ConditionUtil;
import pro.fazeclan.river.jarona.util.GameUtil;
import pro.fazeclan.river.jarona.util.NicknameUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SeerRole extends AbstractInnocentRole {

    private final Map<UUID, UUID> seerToPlayerMap = new HashMap<>();

    public SeerRole(Deceit plugin) {
        super(plugin, "seer");
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.ALLAY_SPAWN_EGG.createItemStack();
    }

    @Override
    public String getPrefix() {
        return "<" + getMiniMessageColor() + ">◆</" + getMiniMessageColor() + ">";
    }

    @EventHandler
    private void seerPlayerIntuition(PlayerInteractAtEntityEvent event) {
        var player = event.getPlayer();
        var world = player.getWorld();
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
        if (!GameUtil.hasGame(world, Deceit.getKey("murder"))) return;
        if (event.getPlayer().getGameMode().isInvulnerable()) return;
        if (!(event.getRightClicked() instanceof Player target)) return;
        var values = GameUtil.getGame(world).getGameValues(world.getUID());
        var role = RoleUtil.getRole(player, values);
        if (role == null) return;
        if (!role.getId().equals(getId())) return;
        var condition = ConditionUtil.getPlayerConditions(player).getOrCreate(
                "seer_intuit",
                new TimedCondition(
                        TimedCondition.Type.GAME_TICK,
                        c -> {
                            var tc = (TimedCondition) c;
                            return getPrefix() + " <" + getMiniMessageColor() + "><b>" + tc.getDuration() / 20 + "s";
                        },
                        player.getUniqueId()
                )
        );
        if (!condition.getAvailable()) return;
        if (seerToPlayerMap.containsKey(player.getUniqueId())) {
            if (!(Bukkit.getEntity(seerToPlayerMap.get(player.getUniqueId())) instanceof Player comparing)) return;
            condition.setDuration(getProperty("intuition-cooldown", 25) * 20);
            condition.setHudCondition((c, _) -> !c.getAvailable());

            var role1 = RoleUtil.getRole(comparing, values);
            var role2 = RoleUtil.getRole(target, values);
            if (role1 == null || role2 == null) {
                player.showTitle(Title.title(
                        Component.empty(),
                        Component.text("No data...").color(NamedTextColor.GRAY)
                ));
                seerToPlayerMap.remove(player.getUniqueId());
                return;
            }

            if (role1.winsWith().equals(role2.winsWith())) {
                player.showTitle(Title.title(
                        Component.empty(),
                        Component.text("They seem to have similar goals!").color(NamedTextColor.GREEN)
                ));
            } else {
                player.showTitle(Title.title(
                        Component.empty(),
                        Component.text("Their goals are different...").color(NamedTextColor.RED)
                ));
            }
            seerToPlayerMap.remove(player.getUniqueId());
        } else {
            seerToPlayerMap.put(player.getUniqueId(), target.getUniqueId());
            player.sendMessage(MiniMessage.miniMessage().deserialize(
                    getPrefix() + " Marked " + NicknameUtil.getNickname(target) + "<reset> for intuition. Interact with another player in order to compare their alignments."
            ));
        }
    }

    @EventHandler
    private void forensicTrackingClear(MurderEndEvent event) {
        for (var p : event.getPlayers()) {
            seerToPlayerMap.remove(p.getUniqueId());
        }
    }

}
