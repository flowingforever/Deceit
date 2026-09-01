package pro.fazeclan.river.deceit.ability.definitions.evil;

import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Mannequin;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.ability.Ability;
import pro.fazeclan.river.deceit.event.AbilityEvent;
import pro.fazeclan.river.deceit.util.MessageUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConcealerAbility extends Ability {

    private final Map<UUID, Location> lastSavedPosition = new HashMap<>();

    public ConcealerAbility(Deceit plugin) {
        super(plugin, "concealer");
    }

    @EventHandler
    private void onSmokeBombUse(AbilityEvent event) {
        if (!event.getExpectedAbility().equals(getId())) return;

        var user = event.getPlayer();
        var stack = event.getItemStack();

        if (user.hasCooldown(stack)) {
            return;
        }
        user.setCooldown(stack, getProperty("cooldown", 30) * 20);
        var savedPos = lastSavedPosition.get(user.getUniqueId());
        if (savedPos != null && savedPos.isWorldLoaded()) {
            var world = user.getWorld();
            user.getWorld().spawnParticle(
                    Particle.CAMPFIRE_COSY_SMOKE,
                    user.getEyeLocation(),
                    500,
                    1,
                    1,
                    1
            );
            world.spawn(user.getLocation(), Mannequin.class, mannequin -> mannequin.setProfile(ResolvableProfile.resolvableProfile(user.getPlayerProfile())));
            user.teleport(savedPos);
            // play enderman tp sound
            user.playSound(
                    user.getLocation(),
                    "minecraft:entity.player.teleport",
                    1f,
                    1f
            );
            lastSavedPosition.remove(user.getUniqueId());
        } else {
            lastSavedPosition.put(user.getUniqueId(), user.getLocation().clone());
            // play save sound (whatever that will be)
            user.playSound(
                    user.getLocation(),
                    "minecraft:entity.player.teleport",
                    1f,
                    2f
            );
            user.sendMessage(MessageUtil.formatComponent(
                    "Saved your current position. Use the item again to teleport back."
            ));
        }
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.ENDER_PEARL.createItemStack();
    }

}
