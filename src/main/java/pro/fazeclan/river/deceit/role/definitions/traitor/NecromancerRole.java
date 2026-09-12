package pro.fazeclan.river.deceit.role.definitions.traitor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.menu.inventory.ShopEntry;
import pro.fazeclan.river.deceit.role.definitions.AbstractTraitorRole;

import java.util.List;

public class NecromancerRole extends AbstractTraitorRole {

    public NecromancerRole(Deceit plugin) {
        super(plugin, "necromancer");
    }

    @Override
    public List<ItemStack> getSpawnItems() {
        return List.of(
                ItemType.BOW.createItemStack(meta -> meta.setUnbreakable(true)),
                ItemType.ARROW.createItemStack(20),
                ItemType.GOLDEN_DANDELION.createItemStack(meta -> {
                    meta.getPersistentDataContainer().set(
                            Deceit.getKey("ability"),
                            PersistentDataType.STRING,
                            "revival"
                    );
                })
        );
    }

    @Override
    public List<ShopEntry> getShopItems() {
        return List.of(
                new ShopEntry(
                        ItemType.ARROW.createItemStack(20),
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
                ),
                new ShopEntry(
                        ItemType.ENDER_EYE.createItemStack(meta -> {
                            meta.getPersistentDataContainer().set(
                                    Deceit.getKey("ability"),
                                    PersistentDataType.STRING,
                                    "concealer"
                            );
                            meta.itemName(Component.text("Concealer"));
                            meta.lore(List.of(
                                    Component.empty(),
                                    Component.text("Save a location and teleport back to it!").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY)
                            ));
                        }),
                        2
                )
        );
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.GOLDEN_CARROT.createItemStack();
    }

}
