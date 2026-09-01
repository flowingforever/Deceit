package pro.fazeclan.river.deceit.role.definitions.traitor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.menu.ShopEntry;
import pro.fazeclan.river.deceit.role.definitions.AbstractTraitorRole;

import java.util.List;

public class TraitorRole extends AbstractTraitorRole {
    public TraitorRole(Deceit plugin) {
        super(plugin, "traitor");
    }

    @Override
    public List<ItemStack> getSpawnItems() {
        return List.of(
                ItemType.BOW.createItemStack(meta -> meta.setUnbreakable(true)),
                ItemType.ARROW.createItemStack(40)
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
                ),
                new ShopEntry(
                        ItemType.CREEPER_HEAD.createItemStack(meta -> {
                            meta.getPersistentDataContainer().set(
                                    Deceit.getKey("ability"),
                                    PersistentDataType.STRING,
                                    "creepanade"
                            );
                            meta.setRarity(ItemRarity.COMMON);
                            meta.itemName(Component.text("Creepanade"));
                        }),
                        2
                ),
                new ShopEntry(
                        ItemType.TNT.createItemStack(meta -> {
                            meta.getPersistentDataContainer().set(
                                    Deceit.getKey("ability"),
                                    PersistentDataType.STRING,
                                    "explosive"
                            );
                            meta.itemName(Component.text("Explosive"));
                        }),
                        2
                ),
                new ShopEntry(
                        ItemType.TORCH.createItemStack(4, meta -> {
                            meta.getPersistentDataContainer().set(
                                    Deceit.getKey("ability"),
                                    PersistentDataType.STRING,
                                    "torch"
                            );
                        }),
                        1
                ),
                new ShopEntry(
                        ItemType.RED_SHULKER_BOX.createItemStack(meta -> {
                            meta.getPersistentDataContainer().set(
                                    Deceit.getKey("ability"),
                                    PersistentDataType.STRING,
                                    "fake_health_kit"
                            );
                            meta.itemName(Component.text("\"Health Kit\""));
                        }),
                        1
                )
        );
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.RED_DYE.createItemStack(meta -> {
            var mm = MiniMessage.miniMessage();
            meta.itemName(mm.deserialize(getName()));
            meta.lore(getDescription().stream().map(mm::deserialize).toList());
        });
    }

    @Override
    public String getPrefix() {
        return "<" + getMiniMessageColor() + ">\uD83E\uDE93</" + getMiniMessageColor() + ">";
    }
}
