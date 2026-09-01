package pro.fazeclan.river.deceit.role.definitions.innocent;

import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.menu.ShopEntry;
import pro.fazeclan.river.deceit.role.definitions.AbstractInnocentRole;

import java.util.List;

public class DoctorRole extends AbstractInnocentRole {

    public DoctorRole(Deceit plugin) {
        super(plugin, "doctor");
    }

    @Override
    public List<ItemStack> getSpawnItems() {
        return List.of(
                ItemType.BOW.createItemStack(meta -> meta.setUnbreakable(true)),
                ItemType.ARROW.createItemStack(20),
                ItemType.IRON_SWORD.createItemStack(meta -> {
                    meta.setUnbreakable(true);
                    meta.itemName(Component.text("Scalpel"));
                    meta.getPersistentDataContainer().set(
                            Deceit.getKey("ability"),
                            PersistentDataType.STRING,
                            "scalpel"
                    );
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
        return ItemType.SPLASH_POTION.createItemStack(meta -> meta.setBasePotionType(PotionType.HEALING));
    }

    @Override
    public String getPrefix() {
        return "<" + getMiniMessageColor() + ">❤</" + getMiniMessageColor() + ">";
    }

}
