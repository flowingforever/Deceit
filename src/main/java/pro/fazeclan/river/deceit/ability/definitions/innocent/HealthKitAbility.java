package pro.fazeclan.river.deceit.ability.definitions.innocent;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Particle;
import org.bukkit.block.BlockType;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.ability.Ability;
import pro.fazeclan.river.deceit.ability.AbilityEvent;
import pro.fazeclan.river.jarona.util.GameUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class HealthKitAbility extends Ability {
    public HealthKitAbility(Deceit plugin) {
        super(plugin, "health_kit");
    }

    @EventHandler
    private void onHealthKitUse(AbilityEvent event) {
        if (!event.getExpectedAbility().equals(getId())) return;

        var player = event.getPlayer();
        var item = event.getItemStack();

        var world = player.getWorld();
        item.setAmount(item.getAmount() - 1);

        world.spawn(player.getLocation().clone().setRotation(0,  0).add(0.0, 1.0, 0.0), Interaction.class, interaction -> {
            interaction.setInteractionHeight(1.0f);
            interaction.setInteractionWidth(1.0f);
            interaction.getScoreboardTags().add("heal");

            world.spawn(interaction.getLocation(), ItemDisplay.class, bd -> {
                bd.setItemStack(ItemType.RED_SHULKER_BOX.createItemStack());
                bd.setTransformation(new Transformation(
                        new Vector3f(),
                        new Quaternionf(),
                        new Vector3f(0.5f, 0.5f, 0.5f),
                        new Quaternionf()
                ));
                interaction.addPassenger(bd);

                AtomicInteger ticks = new AtomicInteger();
                getPlugin().getServer().getScheduler().runTaskTimer(
                        getPlugin(),
                        task -> {
                            if (!bd.isValid()) {
                                task.cancel();
                                return;
                            }

                            // rotate and make it seem like hovering
                            var transformation = bd.getTransformation();
                            float yOffset = (float) (Math.sin(ticks.getAndAdd(1) * 0.1) * 0.25);

                            transformation.getTranslation().set(0, yOffset, 0);
                            transformation.getRightRotation().rotateY((float) Math.toRadians(5));
                            bd.setInterpolationDuration(1);
                            bd.setInterpolationDelay(0);
                            bd.setTransformation(transformation);
                        },
                        1,
                        1
                );
            });
        });
    }

    @EventHandler
    private void onHealthKitInteraction(PlayerInteractAtEntityEvent event) {
        var player = event.getPlayer();
        if (!GameUtil.hasGame(player.getWorld(), Deceit.getKey("murder"))) return;
        if (!(event.getRightClicked() instanceof Interaction interaction)) return;
        if (!interaction.getScoreboardTags().contains("heal")) return;
        if (interaction.getScoreboardTags().contains("on_cooldown")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<red>This health kit is currently on cooldown!</red>"
            ));
            return;
        }

        interaction.getScoreboardTags().add("on_cooldown");
        player.heal(2.0);
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.REGENERATION,
                getProperty("duration", 7) * 20,
                0,
                true,
                true,
                true
        ));
        player.getWorld().spawnParticle(
                Particle.HEART,
                player.getEyeLocation(),
                10,
                1,
                1,
                1
        );
        player.getWorld().playSound(
                player.getLocation(),
                "minecraft:block.brewing_stand.brew",
                1f,
                1f
        );

        getPlugin().getServer().getScheduler().runTaskLater(
                getPlugin(),
                () -> {
                    if (!interaction.isValid()) {
                        return;
                    }

                    interaction.getScoreboardTags().remove("on_cooldown");
                },
                getProperty("cooldown", 10) * 20
        );
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.RED_SHULKER_BOX.createItemStack();
    }
}
