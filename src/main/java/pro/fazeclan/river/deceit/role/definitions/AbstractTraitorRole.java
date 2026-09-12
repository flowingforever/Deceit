package pro.fazeclan.river.deceit.role.definitions;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.menu.inventory.ShopEntry;
import pro.fazeclan.river.deceit.role.Faction;
import pro.fazeclan.river.deceit.role.Role;
import pro.fazeclan.river.deceit.util.MiscUtil;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.jarona.game.GameValues;

import java.util.List;

public abstract class AbstractTraitorRole extends Role {

    public AbstractTraitorRole(Deceit plugin, String id) {
        super(plugin, id, Faction.TRAITOR, false);
    }

    @Override
    public boolean hasWon(List<Player> players, GameValues values) {
        return !RoleUtil.isTeamAlive(players, values, Faction.INNOCENT)
                && values.getValue("time_limit", 0L) > values.getValue("tick", 0L);
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
                ),
                new ShopEntry(
                        ItemType.ENDER_EYE.createItemStack(meta -> {
                            meta.getPersistentDataContainer().set(
                                    Deceit.getKey("ability"),
                                    PersistentDataType.STRING,
                                    "teleporter"
                            );
                            meta.itemName(Component.text("Teleporter"));
                            meta.lore(List.of(
                                    Component.empty(),
                                    Component.text("Save a location and teleport back to it!").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY)
                            ));
                        }),
                        2
                ),
                new ShopEntry(
                        MiscUtil.createItem(Material.POTION, 1, stack -> {
                            stack.editMeta(meta -> {
                                meta.getPersistentDataContainer().set(
                                        Deceit.getKey("ability"),
                                        PersistentDataType.STRING,
                                        "swoop"
                                );
                                meta.customName(Component.text("Swoop").decoration(TextDecoration.ITALIC, false));
                                ((PotionMeta) meta).setBasePotionType(PotionType.INVISIBILITY);
                            });
                            stack.unsetData(DataComponentTypes.CONSUMABLE);
                        }),
                        2
                ),
                new ShopEntry(
                        MiscUtil.createItem(Material.SLIME_BALL, 1, stack -> stack.editMeta(meta -> {
                            meta.getPersistentDataContainer().set(
                                    Deceit.getKey("ability"),
                                    PersistentDataType.STRING,
                                    "muzzle"
                            );
                            meta.itemName(Component.text("Muzzle"));
                        })),
                        2
                )
        );
    }

    @Override
    public List<ItemStack> getSpawnItems() {
        return List.of(
                ItemType.BOW.createItemStack(meta -> meta.setUnbreakable(true)),
                ItemType.ARROW.createItemStack(30)
        );
    }

    @Override
    public String winsWith() {
        return "traitor";
    }

}
