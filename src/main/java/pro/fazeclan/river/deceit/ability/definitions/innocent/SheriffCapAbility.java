package pro.fazeclan.river.deceit.ability.definitions.innocent;

import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.ability.Ability;
import pro.fazeclan.river.jarona.util.GameUtil;

public class SheriffCapAbility extends Ability {
    public SheriffCapAbility(Deceit plugin) {
        super(plugin, "sheriff_cap");
    }

    @EventHandler
    private void onCapEquip(EntityEquipmentChangedEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!GameUtil.hasGame(player.getWorld(), Deceit.getKey("murder"))) {
            return;
        }
        if (event.getEquipmentChanges()
                .entrySet()
                .stream()
                .noneMatch(
                        entry -> entry.getKey().isArmor() && hasAbility(entry.getValue().newItem())
                )
        ) {
            return;
        }
        var values = GameUtil.getGame(player).getGameValues(player.getWorld().getUID());
        values.setValue(
                "revealed_" + player.getUniqueId(),
                true
        );
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.COPPER_HELMET.createItemStack();
    }
}
