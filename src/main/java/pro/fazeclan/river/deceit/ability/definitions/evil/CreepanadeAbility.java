package pro.fazeclan.river.deceit.ability.definitions.evil;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.SulfurCube;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.ability.Ability;
import pro.fazeclan.river.deceit.event.AbilityEvent;
import pro.fazeclan.river.deceit.util.ExplosiveUtil;

public class CreepanadeAbility extends Ability {

    public CreepanadeAbility(Deceit plugin) {
        super(plugin, "creepanade");
    }

    @EventHandler
    private void onCreepanadeUse(AbilityEvent event) {
        if (!event.getExpectedAbility().equals(getId())) return;
        var player = event.getPlayer();
        var item = event.getItemStack();

        player.setCooldown(item, getProperty("cooldown", 200));
        var world = player.getWorld();
        world.playSound(player.getLocation(), "minecraft:entity.creeper.hurt", 1.0f, 1.0f);
        world.spawn(player.getEyeLocation(), SulfurCube.class, sc -> {
            sc.setAware(false);
            sc.getEquipment().setItem(EquipmentSlot.BODY, ItemType.CREEPER_HEAD.createItemStack());
            sc.setVelocity(player.getLocation().getDirection());
            sc.addPotionEffect(new PotionEffect(
                    PotionEffectType.INVISIBILITY,
                    -1,
                    0,
                    true,
                    false
            ));
            sc.getAttribute(Attribute.SCALE).setBaseValue(0.5);
            sc.getAttribute(Attribute.AIR_DRAG_MODIFIER).setBaseValue(0.5);
            sc.getAttribute(Attribute.GRAVITY).setBaseValue(0.05);
            sc.getAttribute(Attribute.BOUNCINESS).setBaseValue(1);

            final double radius = getProperty("radius", 5.0);
            final double damage = getProperty("damage", 20.0);
            final double minimumDamage = getProperty("minimum-damage", 5.0);
            final double velocityMultiplier = getProperty("velocity-multiplier", 1.1);
            int delay = getProperty("delay", 5);
            getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), () -> {
                if (!sc.isValid()) {
                    return;
                }

                var loc = sc.getLocation().clone();
                sc.setGravity(false);
                sc.setVelocity(new Vector());
                ExplosiveUtil.handleExplosionBuildup(getPlugin(), player, loc, radius, damage, minimumDamage, velocityMultiplier, delay);
                getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), sc::remove, getPlugin().getConfig().getInt("grenade-delay", 5));
            }, 60);
        });
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.CREEPER_SPAWN_EGG.createItemStack();
    }

}
