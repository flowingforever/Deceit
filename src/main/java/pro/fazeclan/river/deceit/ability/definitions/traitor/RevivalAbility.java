package pro.fazeclan.river.deceit.ability.definitions.traitor;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Mannequin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.ability.Ability;
import pro.fazeclan.river.deceit.role.Faction;
import pro.fazeclan.river.deceit.util.GameFunctions;
import pro.fazeclan.river.deceit.util.GlowUtil;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.game.GameValues;
import pro.fazeclan.river.jarona.util.GameUtil;

import java.util.ArrayList;
import java.util.Collections;

public class RevivalAbility extends Ability {

    public RevivalAbility(Deceit plugin) {
        super(plugin, "revival");
    }

    @EventHandler
    private void onRevivalAttempt(PlayerInteractAtEntityEvent event) {
        var player = event.getPlayer();
        var item = player.getEquipment().getItem(event.getHand());
        if (event.getPlayer().getGameMode().isInvulnerable()) return;
        if (!hasAbility(item)) return;
        if (!(event.getRightClicked() instanceof Mannequin corpse)) return;
        if (corpse.getVisualFire().equals(TriState.TRUE)) return;
        var world = player.getWorld();
        var values = GameUtil.getGame(player).getGameValues(world.getUID());
        var mm = MiniMessage.miniMessage();
        var role = RoleUtil.getRole(player, values);
        if (countPlayersInTraitor(world, values) - countAlivePlayersInTraitor(world, values) < 1) {
            player.sendMessage(mm.deserialize(
                    role.getPrefix() + " All of your teammates are alive. Maybe try this when one is dead?"
            ));
            return;
        }
        corpse.setVisualFire(TriState.TRUE);
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
                var roles = new ArrayList<>(getPlugin().getRoleManager().getUnlimitedRoles(Faction.TRAITOR));
                Collections.shuffle(roles);
                var selectedRole = roles.getFirst();
                revived.teleport(corpse);
                revived.setGameMode(GameMode.ADVENTURE);
                Jarona.getInstance().getVoicechatPlugin().removePlayer(revived);
                corpse.remove();
                GameFunctions.assignRole(revived, selectedRole, values);
                player.sendMessage(mm.deserialize(
                        role.getPrefix() + " This player has been resurrected as " + selectedRole.getPrefix() + "<" + selectedRole.getMiniMessageColor() + "> " + selectedRole.getName() + "!"
                ));
                revived.sendMessage(mm.deserialize(
                        role.getPrefix() + " You have been resurrected as " + selectedRole.getPrefix() + "<" + selectedRole.getMiniMessageColor() + "> " + selectedRole.getName() + "!"
                ));
                GlowUtil.setGlowOfPlayerToWorld(world, revived, NamedTextColor.BLACK);
            });
        });
    }

    private long countAlivePlayersInTraitor(World world, GameValues values) {
        return world.getPlayers()
                .stream()
                .filter(player -> !player.getGameMode().isInvulnerable())
                .filter(player -> RoleUtil.getRole(player, values) != null)
                .filter(player -> !RoleUtil.getRole(player, values).winsWith().equals("traitor"))
                .count();
    }

    private long countPlayersInTraitor(World world, GameValues values) {
        return world.getPlayers()
                .stream()
                .filter(player -> RoleUtil.getRole(player, values) != null)
                .filter(player -> !RoleUtil.getRole(player, values).winsWith().equals("traitor"))
                .count();
    }

    @Override
    public ItemStack getDisplayItem() {
        return null;
    }

}
