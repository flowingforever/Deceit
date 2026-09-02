package pro.fazeclan.river.deceit.role.definitions.neutral;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.menu.ShopEntry;
import pro.fazeclan.river.deceit.role.definitions.AbstractNeutralRole;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.jarona.game.GameValues;

import java.util.List;

public class PyromaniacRole extends AbstractNeutralRole {

    public PyromaniacRole(Deceit plugin) {
        super(plugin, "pyromaniac");
    }

    @Override
    public List<ItemStack> getSpawnItems() {
        return List.of(
                ItemType.BOW.createItemStack(meta -> meta.setUnbreakable(true)),
                ItemType.ARROW.createItemStack(15),
                ItemType.MAGMA_CREAM.createItemStack(meta -> {
                    meta.getPersistentDataContainer().set(
                            Deceit.getKey("ability"),
                            PersistentDataType.STRING,
                            "douse"
                    );
                    meta.itemName(Component.text("Magma Energy"));
                    meta.lore(List.of(
                            Component.empty(),
                            Component.text("Interact with players using this").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY),
                            Component.text("to douse them in magma energy.").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY),
                            Component.text("Once the name is a bright orange,").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY),
                            Component.text("you may interact with any player").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY),
                            Component.text("in order to instantly kill them.").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY)
                    ));
                })
        );
    }

    @Override
    public List<ShopEntry> getShopItems() {
        return List.of(
                new ShopEntry(
                        ItemType.STONE_SWORD.createItemStack(meta -> {
                            meta.setUnbreakable(true);
                            meta.addEnchant(Enchantment.SHARPNESS, 1, true);
                        }),
                        2
                ),
                new ShopEntry(
                        ItemType.IRON_SWORD.createItemStack(meta -> {
                            meta.setUnbreakable(true);
                            meta.itemName(Component.text("Dagger"));
                            meta.lore(List.of(
                                    Component.empty(),
                                    Component.text("\"Perfect for stabbing people in the back!\"").color(NamedTextColor.GRAY),
                                    Component.text(" - Shakespeare, probably").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                            ));
                            meta.getPersistentDataContainer().set(
                                    Deceit.getKey("ability"),
                                    PersistentDataType.STRING,
                                    "backstab"
                            );
                        }),
                        2
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
                ),
                new ShopEntry(
                        ItemType.COMPASS.createItemStack(meta -> {
                            meta.getPersistentDataContainer().set(
                                    Deceit.getKey("ability"),
                                    PersistentDataType.STRING,
                                    "tracker"
                            );
                            meta.itemName(Component.text("Tracker"));
                        }),
                        1
                )
        );
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.BLAZE_ROD.createItemStack();
    }

    @Override
    public boolean hasWon(List<Player> players, GameValues values) {
        return RoleUtil.onlyPlayersInTeamRemain(players, values, winsWith());
    }

    @Override
    public String getPrefix() {
        return "<" + getMiniMessageColor() + ">\uD83D\uDD25</" + getMiniMessageColor() + ">";
    }

}
