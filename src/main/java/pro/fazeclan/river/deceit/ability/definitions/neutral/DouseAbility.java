package pro.fazeclan.river.deceit.ability.definitions.neutral;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.ability.Ability;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.jarona.game.GameValues;
import pro.fazeclan.river.jarona.util.GameUtil;

public class DouseAbility extends Ability {

    public DouseAbility(Deceit plugin) {
        super(plugin, "douse");
    }

    @EventHandler
    private void onDouse(PlayerInteractAtEntityEvent event) {
        var player = event.getPlayer();
        var item = player.getEquipment().getItem(event.getHand());
        if (!hasAbility(item)) return;
        if (!(event.getRightClicked() instanceof Player doused)) return;
        var values = GameUtil.getGame(player).getGameValues(player.getWorld().getUID());
        if (player.hasCooldown(item)) return;
        player.setCooldown(item, getProperty("cooldown", 20) * 20);
        var role = getPlugin().getRoleManager().getRole("pyromaniac");
        if (values.getValue("doused_" + doused.getUniqueId(), false)) {
            if (allAlivePlayersDoused(player.getWorld(), values)) {
                player.playSound(
                        doused.getLocation(),
                        "minecraft:block.fire.extinguish",
                        SoundCategory.PLAYERS,
                        1f,
                        0.7f
                );

                doused.playSound(
                        doused.getLocation(),
                        "minecraft:block.fire.extinguish",
                        SoundCategory.PLAYERS,
                        1f,
                        0.7f
                );
                doused.damage(2000, DamageSource.builder(DamageType.IN_FIRE).withCausingEntity(player).withDirectEntity(player).build());
            }
        } else {
            values.setValue("doused_" + doused.getUniqueId(), true);
            player.sendMessage(MiniMessage.miniMessage().deserialize(
                    role.getPrefix() + " You've doused " + doused.getName() + "!"
            ));
            player.playSound(
                    player.getLocation(),
                    "minecraft:block.brewing_stand.brew",
                    SoundCategory.PLAYERS,
                    1f,
                    0.5f
            );
            if (allAlivePlayersDoused(player.getWorld(), values)) {
                item.editMeta(meta -> meta.itemName(meta.itemName().color(TextColor.fromHexString(role.getMiniMessageColor()))));
            }
        }
    }

    private long countAlivePlayersDoused(World world, GameValues values) {
        return world.getPlayers()
                .stream()
                .filter(player -> RoleUtil.getRole(player, values) != null)
                .filter(player -> !RoleUtil.getRole(player, values).winsWith().equals("pyromaniac"))
                .filter(player -> values.getValue("doused_" + player.getUniqueId(), false))
                .count();
    }

    private boolean allAlivePlayersDoused(World world, GameValues values) {
        return world.getPlayers()
                .stream()
                .filter(player -> RoleUtil.getRole(player, values) != null)
                .filter(player -> !RoleUtil.getRole(player, values).winsWith().equals("pyromaniac"))
                .allMatch(player -> values.getValue("doused_" + player.getUniqueId(), false));
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.BLAZE_ROD.createItemStack();
    }

}
