package pro.fazeclan.river.deceit.role.definitions.innocent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.menu.ShopEntry;
import pro.fazeclan.river.deceit.role.definitions.AbstractInnocentRole;

import java.util.List;

public class SheriffRole extends AbstractInnocentRole {
    public SheriffRole(Deceit plugin) {
        super(plugin, "sheriff");
    }

    @Override
    public List<ItemStack> getSpawnItems() {
        return List.of(
                ItemType.BOW.createItemStack(meta -> meta.setUnbreakable(true)),
                ItemType.ARROW.createItemStack(30)
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
                        ItemType.LIGHTNING_ROD.createItemStack(meta -> {
                            meta.itemName(Component.text("Scanner"));
                            meta.lore(List.of(
                                    Component.text("Scans in a small radius for traitors.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                            ));
                            meta.getPersistentDataContainer().set(
                                    Deceit.getKey("ability"),
                                    PersistentDataType.STRING,
                                    "scanner"
                            );
                        }),
                        1
                ),
                new ShopEntry(
                        ItemType.COPPER_HELMET.createItemStack(meta -> {
                            meta.itemName(Component.text("Sheriff's Cap"));
                            meta.addEnchant(
                                    Enchantment.BINDING_CURSE,
                                    1,
                                    true
                            );
                            meta.setUnbreakable(true);
                            meta.getPersistentDataContainer().set(
                                    Deceit.getKey("ability"),
                                    PersistentDataType.STRING,
                                    "sheriff_cap"
                            );
                        }),
                        1
                ),
                new ShopEntry(
                        ItemType.RED_SHULKER_BOX.createItemStack(meta -> {
                            meta.getPersistentDataContainer().set(
                                    Deceit.getKey("ability"),
                                    PersistentDataType.STRING,
                                    "health_kit"
                            );
                            meta.itemName(Component.text("Health Kit"));
                        }),
                        1
                )
        );
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.BOW.createItemStack(meta -> {
            var mm = MiniMessage.miniMessage();
            meta.itemName(mm.deserialize(getName()));
            meta.lore(getDescription().stream().map(mm::deserialize).toList());
        });
    }

    @Override
    public String getPrefix() {
        return "<" + getMiniMessageColor() + ">\uD83C\uDFF9</" + getMiniMessageColor() + ">";
    }
}
