package pro.fazeclan.river.deceit.ability.definitions.traitor;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.ability.Ability;
import pro.fazeclan.river.deceit.event.AbilityEvent;

public class SwoopAbility extends Ability {

    public SwoopAbility(Deceit plugin) {
        super(plugin, "swoop");
    }

    @EventHandler
    private void onSwoopAbility(AbilityEvent event) {
        if (!event.getExpectedAbility().equals(getId())) return;

        var user = event.getPlayer();
        var world = user.getWorld();
        var stack = event.getItemStack();

        if (user.hasCooldown(stack)) return;
        user.setCooldown(stack, getProperty("cooldown", 30) * 20);

        user.addPotionEffect(new PotionEffect(
                PotionEffectType.INVISIBILITY,
                getProperty("duration", 7) * 20,
                0,
                true,
                false,
                true
        ));
        user.setArrowsInBody(0);

        world.spawnParticle(
                Particle.ENTITY_EFFECT,
                user.getEyeLocation(),
                20,
                1,
                1,
                1,
                Color.WHITE
        );
        user.playSound(
                user.getLocation(),
                "minecraft:block.brewing_stand.brew",
                SoundCategory.PLAYERS,
                1f,
                1f
        );
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.POTION.createItemStack(meta -> {
            meta.setBasePotionType(PotionType.INVISIBILITY);
        });
    }

}
