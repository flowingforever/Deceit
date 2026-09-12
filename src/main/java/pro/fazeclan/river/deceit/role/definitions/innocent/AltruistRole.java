package pro.fazeclan.river.deceit.role.definitions.innocent;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Mannequin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.role.definitions.AbstractInnocentRole;
import pro.fazeclan.river.deceit.util.GameFunctions;
import pro.fazeclan.river.deceit.util.GlowUtil;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.condition.TimedCondition;
import pro.fazeclan.river.jarona.util.ConditionUtil;
import pro.fazeclan.river.jarona.util.GameUtil;
import pro.fazeclan.river.jarona.util.NicknameUtil;

public class AltruistRole extends AbstractInnocentRole {
    public AltruistRole(Deceit plugin) {
        super(plugin, "altruist");
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.GHAST_SPAWN_EGG.createItemStack();
    }

    @EventHandler
    private void altruistRevivalAttempt(PlayerInteractAtEntityEvent event) {
        var player = event.getPlayer();
        var world = player.getWorld();
        if (!GameUtil.hasGame(world, Deceit.getKey("murder"))) return;
        if (event.getPlayer().getGameMode().isInvulnerable()) return;
        if (!(event.getRightClicked() instanceof Mannequin corpse)) return;
        var values = GameUtil.getGame(world).getGameValues(world.getUID());
        var role = RoleUtil.getRole(player, values);
        if (role == null) return;
        if (!role.getId().equals(getId())) return;
        var condition = ConditionUtil.getPlayerConditions(player).getOrCreate(
                "altruist_revival",
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
        corpse.setVisualFire(TriState.TRUE);
        condition.setDuration(1000);
        var mm = MiniMessage.miniMessage();
        corpse.getProfile().resolve().thenAcceptAsync(profile -> {
            if (profile.getId() == null) return;
            Bukkit.getScheduler().runTask(getPlugin(), () -> {
                var revived = Bukkit.getPlayer(profile.getId());
                if (revived == null) {
                    player.sendMessage(mm.deserialize(
                            role.getPrefix() + " This player is not online, find another body to resurrect!"
                    ));
                    return;
                }
                revived.teleport(corpse);
                revived.setGameMode(GameMode.ADVENTURE);
                Jarona.getInstance().getVoicechatPlugin().removePlayer(revived);
                corpse.remove();
                player.sendMessage(mm.deserialize(
                        role.getPrefix() + " You sacrificed yourself to resurrect " + NicknameUtil.getNickname(revived) + "<reset>!"
                ));
                GameFunctions.eliminatePlayer(player, getProperty("reveals-on-sacrifice", false), true);
                revived.sendMessage(mm.deserialize(
                        role.getPrefix() + " You have been resurrected by an " + getPrefix() + "<" + getMiniMessageColor() + ">" + getName() + "!"
                ));
                GlowUtil.setGlowOfPlayerToWorld(world, revived, NamedTextColor.BLACK);
            });
        });
    }

}
