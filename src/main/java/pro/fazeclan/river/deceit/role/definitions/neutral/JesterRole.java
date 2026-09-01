package pro.fazeclan.river.deceit.role.definitions.neutral;

import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.menu.ShopEntry;
import pro.fazeclan.river.deceit.role.definitions.AbstractNeutralRole;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.jarona.game.GameValues;
import pro.fazeclan.river.jarona.util.GameUtil;

import java.util.List;

public class JesterRole extends AbstractNeutralRole {

    public JesterRole(Deceit plugin) {
        super(plugin, "jester");
    }

    @Override
    public List<ItemStack> getSpawnItems() {
        return List.of(
                ItemType.BOW.createItemStack(meta -> meta.setUnbreakable(true)),
                ItemType.ARROW.createItemStack(20)
        );
    }

    @Override
    public List<ShopEntry> getShopItems() {
        return List.of(
                new ShopEntry(
                        ItemType.IRON_SWORD.createItemStack(meta -> {
                            meta.setUnbreakable(true);
                            meta.itemName(Component.text("Fake Dagger"));
                            meta.getPersistentDataContainer().set(
                                    Deceit.getKey("ability"),
                                    PersistentDataType.STRING,
                                    "fake_dagger"
                            );
                        }),
                        1
                ),
                new ShopEntry(
                        ItemType.WOODEN_SWORD.createItemStack(meta -> meta.setUnbreakable(true)),
                        1
                ),
                new ShopEntry(
                        List.of(
                                ItemType.BOW.createItemStack(meta -> {
                                    meta.setUnbreakable(true);
                                    meta.addEnchant(Enchantment.POWER, 1, true);
                                }),
                                ItemType.ARROW.createItemStack(20)
                        ),
                        2
                ),
                new ShopEntry(
                        ItemType.POTION.createItemStack(meta -> meta.setBasePotionType(PotionType.SWIFTNESS)),
                        1
                ),
                new ShopEntry(
                        ItemType.SPLASH_POTION.createItemStack(meta -> meta.setBasePotionType(PotionType.HEALING)),
                        1
                )
        );
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.SPECTRAL_ARROW.createItemStack();
    }

    @Override
    public boolean hasWon(List<Player> players, GameValues values) {
        return values.getValue("jester_won", false);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onJesterDeath(EntityDamageByEntityEvent event) {
        handleJesterAttack(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onJesterDeath(EntityDamageEvent event) {
        handleJesterAttack(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onJesterDeath(EntityDamageByBlockEvent event) {
        handleJesterAttack(event);
    }

    private void handleJesterAttack(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamageSource().getCausingEntity() instanceof Player attacker)) return;
        if (!victim.getGameMode().isInvulnerable()) return;
        if (!GameUtil.hasGame(victim.getWorld(), Deceit.getKey("murder"))) return;
        var values = GameUtil.getGame(victim.getWorld()).getGameValues(victim.getWorld().getUID());
        if (RoleUtil.isEvil(attacker, values)) return;
        if (!this.equals(RoleUtil.getRole(victim, values))) return;
        values.setValue("jester_won", true);
    }

    @Override
    public String getPrefix() {
        return "<" + getMiniMessageColor() + ">☻</" + getMiniMessageColor() + ">";
    }

}
