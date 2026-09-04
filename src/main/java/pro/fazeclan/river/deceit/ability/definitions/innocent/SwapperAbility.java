package pro.fazeclan.river.deceit.ability.definitions.innocent;

import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.ability.Ability;
import pro.fazeclan.river.deceit.event.AbilityEvent;
import pro.fazeclan.river.deceit.menu.dialog.SwapperDialogMenu;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.jarona.condition.TimedCondition;
import pro.fazeclan.river.jarona.util.ConditionUtil;
import pro.fazeclan.river.jarona.util.GameUtil;

public class SwapperAbility extends Ability {

    public SwapperAbility(Deceit plugin) {
        super(plugin, "swapper");
    }

    @EventHandler
    private void onSwapperCall(AbilityEvent event) {
        if (!event.getExpectedAbility().equals(getId())) return;
        var player = event.getPlayer();
        var values = GameUtil.getGame(player).getGameValues(player.getWorld().getUID());
        var role = RoleUtil.getRole(player, values);
        var condition = ConditionUtil.getPlayerConditions(player)
                .getOrCreate(
                        "swapper_swapping",
                        new TimedCondition(
                                TimedCondition.Type.GAME_TICK,
                                c -> {
                                    var tc = (TimedCondition) c;
                                    return role.getPrefix() + " <" + role.getMiniMessageColor() + "><b>" + tc.getDuration() / 20 + "s";
                                },
                                player.getUniqueId()
                        )
                );
        if (!condition.getAvailable()) return;
        player.setCooldown(event.getItemStack(), 60); // prevent fast uses of the item
        SwapperDialogMenu.openMenu(player, player.getWorld(), values, condition, getProperty("cooldown", 30), null);
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.DISPENSER.createItemStack();
    }

}
