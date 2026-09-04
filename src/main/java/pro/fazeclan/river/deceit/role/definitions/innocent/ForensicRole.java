package pro.fazeclan.river.deceit.role.definitions.innocent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.event.MurderEndEvent;
import pro.fazeclan.river.deceit.event.MurderPostEliminationEvent;
import pro.fazeclan.river.deceit.event.MurderPreEliminationEvent;
import pro.fazeclan.river.deceit.event.MurderTickEvent;
import pro.fazeclan.river.deceit.role.definitions.AbstractInnocentRole;
import pro.fazeclan.river.deceit.util.MiscUtil;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.jarona.condition.TimedCondition;
import pro.fazeclan.river.jarona.util.ConditionUtil;
import pro.fazeclan.river.jarona.util.GameUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ForensicRole extends AbstractInnocentRole {

    private final Map<UUID, UUID> forensicToCorpseMap = new HashMap<>();

    public ForensicRole(Deceit plugin) {
        super(plugin, "forensic");
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.SPYGLASS.createItemStack();
    }

    @EventHandler
    private void forensicCorpseInspection(PlayerInteractAtEntityEvent event) {
        var player = event.getPlayer();
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
        if (event.getPlayer().getGameMode().isInvulnerable()) return;
        if (!(event.getRightClicked() instanceof Mannequin corpse)) return;
        if (corpse.getVisualFire().equals(TriState.TRUE)) return;
        var world = player.getWorld();
        if (!GameUtil.hasGame(world, Deceit.getKey("murder"))) return;
        var values = GameUtil.getGame(world).getGameValues(world.getUID());
        var role = RoleUtil.getRole(player, values);
        if (role == null) return;
        if (!role.getId().equals(getId())) return;
        forensicToCorpseMap.put(player.getUniqueId(), corpse.getUniqueId());
        player.sendMessage(MiniMessage.miniMessage().deserialize(
                getPrefix() + " Marked this corpse for inspection. Interact with any player to see if they were at the scene at any time."
        ));
    }

    @EventHandler
    private void forensicPlayerExamination(PlayerInteractAtEntityEvent event) {
        var player = event.getPlayer();
        var world = player.getWorld();
        if (!GameUtil.hasGame(world, Deceit.getKey("murder"))) return;
        if (event.getPlayer().getGameMode().isInvulnerable()) return;
        if (!(event.getRightClicked() instanceof Player target)) return;
        var values = GameUtil.getGame(world).getGameValues(world.getUID());
        var role = RoleUtil.getRole(player, values);
        if (role == null) return;
        if (!role.getId().equals(getId())) return;
        var condition = ConditionUtil.getPlayerConditions(player).getOrCreate(
                "forensic_examine",
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
        if (!forensicToCorpseMap.containsKey(player.getUniqueId())) return;
        if (!(Bukkit.getEntity(forensicToCorpseMap.get(player.getUniqueId())) instanceof Mannequin corpse)) return;
        var playerList = corpse.getScoreboardTags()
                .stream()
                .map(MiscUtil::fromStringOrNull)
                .filter(Objects::nonNull)
                .toList();
        condition.setDuration(getProperty("examine-cooldown", 25) * 20);
        condition.setHudCondition((c, _) -> !c.getAvailable());
        if (playerList.contains(target.getUniqueId())) {
            player.showTitle(Title.title(
                    Component.empty(),
                    Component.text("!! AT THE SCENE !!").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)
            ));
            player.playSound(
                    player.getLocation(),
                    "minecraft:block.note_block.bit",
                    SoundCategory.PLAYERS,
                    1f,
                    0.5f
            );
        } else {
            player.showTitle(Title.title(
                    Component.empty(),
                    Component.text("!! NOT AT THE SCENE !!").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
            ));
            player.playSound(
                    player.getLocation(),
                    "minecraft:block.note_block.bit",
                    SoundCategory.PLAYERS,
                    1f,
                    2f
            );
        }
    }

    @EventHandler
    private void ensureForensicCorpseTracking(MurderTickEvent event) {
        var world = event.getWorld();
        var radius = getProperty("corpse-detection-radius", 4.0);
        for (var corpse : world.getEntitiesByClass(Mannequin.class)) {
            var nearbyPlayers = corpse.getNearbyEntities(radius, radius, radius)
                    .stream()
                    .filter(entity -> entity instanceof Player p && !p.getGameMode().isInvulnerable())
                    .map(e -> e.getUniqueId().toString())
                    .toList();
            corpse.getScoreboardTags().addAll(nearbyPlayers);
        }
    }

    @EventHandler
    private void forensicTrackingClear(MurderEndEvent event) {
        for (var p : event.getPlayers()) {
            forensicToCorpseMap.remove(p.getUniqueId());
        }
    }

    @Override
    public String getPrefix() {
        return "<" + getMiniMessageColor() + ">◆</" + getMiniMessageColor() + ">";
    }

}
