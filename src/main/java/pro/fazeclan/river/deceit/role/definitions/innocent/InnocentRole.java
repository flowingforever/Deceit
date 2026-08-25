package pro.fazeclan.river.deceit.role.definitions.innocent;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.menu.ShopEntry;
import pro.fazeclan.river.deceit.role.definitions.AbstractInnocentRole;

import java.util.List;

public class InnocentRole extends AbstractInnocentRole {

    public InnocentRole(Deceit plugin) {
        super(plugin, "innocent");
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
                        ItemType.STONE_SWORD.createItemStack(meta -> {
                            meta.setUnbreakable(true);
                            meta.addEnchant(Enchantment.SHARPNESS, 1, true);
                        }),
                        2
                ),
                new ShopEntry(
                        ItemType.BOW.createItemStack(meta -> {
                            meta.setUnbreakable(true);
                            meta.addEnchant(Enchantment.POWER, 1, true);
                        }),
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
        return ItemType.GREEN_DYE.createItemStack(meta -> {
            var mm = MiniMessage.miniMessage();
            meta.itemName(mm.deserialize(getName()));
            meta.lore(getDescription().stream().map(mm::deserialize).toList());
        });
    }

    @Override
    public String getPrefix() {
        return "<green>◆</green>";
    }
}
