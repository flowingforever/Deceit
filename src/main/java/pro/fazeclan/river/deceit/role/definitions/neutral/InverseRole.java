package pro.fazeclan.river.deceit.role.definitions.neutral;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.event.MurderEliminationEvent;
import pro.fazeclan.river.deceit.menu.ShopEntry;
import pro.fazeclan.river.deceit.role.Faction;
import pro.fazeclan.river.deceit.role.definitions.AbstractNeutralRole;
import pro.fazeclan.river.deceit.util.GameFunctions;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.jarona.game.GameValues;
import pro.fazeclan.river.jarona.util.GameUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InverseRole extends AbstractNeutralRole {
    public InverseRole(Deceit plugin) {
        super(plugin, "inverse");
    }

    @Override
    public List<ItemStack> getSpawnItems() {
        return List.of(
                ItemType.BOW.createItemStack(meta -> meta.setUnbreakable(true)),
                ItemType.ARROW.createItemStack(30),
                ItemType.STONE_SWORD.createItemStack(meta -> {
                    meta.setUnbreakable(true);
                    meta.addEnchant(Enchantment.SHARPNESS, 1, true);
                })
        );
    }

    @Override
    public List<ShopEntry> getShopItems() {
        return List.of();
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.MAGENTA_GLAZED_TERRACOTTA.createItemStack();
    }

    @Override
    public boolean hasWon(List<Player> players, GameValues values) {
        return false; // cannot win as this role
    }

    @EventHandler
    private void onInverseRole(MurderEliminationEvent event) {
        if (!(event.getSource().getCausingEntity() instanceof Player attacker)) return;
        var victim = event.getEliminated();
        if (!GameUtil.hasGame(victim.getWorld(), Deceit.getKey("murder"))) return;
        var values = GameUtil.getGame(victim.getWorld()).getGameValues(victim.getWorld().getUID());
        var attackerRole = RoleUtil.getRole(attacker, values);
        if (attackerRole == null) return;
        if (!attackerRole.winsWith().equals(winsWith())) return;
        if (RoleUtil.isTraitor(victim, values)) {
            assignRandomRole(attacker, values, Faction.INNOCENT);
        } else if (RoleUtil.isInnocent(victim, values)) {
            assignRandomRole(attacker, values, Faction.TRAITOR);
        } else {
            attacker.sendMessage(MiniMessage.miniMessage().deserialize(
                    getPrefix() + " This kill wasn't very appealing. <yellow>Maybe you should look for another?</yellow>"
            ));
        }
    }

    private void assignRandomRole(Player attacker, GameValues values, Faction faction) {
        var manager = getPlugin().getRoleManager();
        var roles = new ArrayList<>(manager.getUnlimitedRoles(faction));
        Collections.shuffle(roles);
        var role = roles.getFirst();
        GameFunctions.assignRole(attacker, role, values);
        attacker.sendMessage(MiniMessage.miniMessage().deserialize(
                getPrefix() + " With this successful kill, you've been assigned as " + role.getPrefix() + " " + role.getName() + "!"
        ));
    }

    @Override
    public String getPrefix() {
        return "<" + getMiniMessageColor() + ">◇</" + getMiniMessageColor() + ">";
    }
}
