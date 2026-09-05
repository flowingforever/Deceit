package pro.fazeclan.river.deceit.role.definitions.traitor;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.event.MurderPreEliminationEvent;
import pro.fazeclan.river.deceit.role.definitions.AbstractTraitorRole;
import pro.fazeclan.river.deceit.util.GameFunctions;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.jarona.util.GameUtil;

import java.util.List;

public class UnderlingRole extends AbstractTraitorRole {

    public UnderlingRole(Deceit plugin) {
        super(plugin, "underling");
    }

    @EventHandler
    private void onTraitorDeath(MurderPreEliminationEvent event) {
        var eliminated = event.getEliminated();
        var world = eliminated.getWorld();
        var values = GameUtil.getGame(world).getGameValues(world.getUID());
        if (!RoleUtil.isTraitor(event.getEliminated(), values)) return;
        var mm = MiniMessage.miniMessage();
        for (var underling : RoleUtil.getAllWithRole(world, values, getId())) {
            GameFunctions.giveBells(underling, values, 1);
            underling.sendMessage(mm.deserialize(
                    getPrefix() + " You have been awarded a bell due to a loss of a teammate."
            ));
        }
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.COPPER_SWORD.createItemStack();
    }

    @Override
    public String getPrefix() {
        return "<" + getMiniMessageColor() + ">\uD83E\uDE93</" + getMiniMessageColor() + ">";
    }
}
