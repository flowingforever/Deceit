package pro.fazeclan.river.deceit.ability.definitions.evil;

import net.kyori.adventure.util.TriState;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Mannequin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.ability.Ability;

public class TorchAbility extends Ability {

    public TorchAbility(Deceit plugin) {
        super(plugin, "torch");
    }

    @EventHandler
    private void onTorchCorpseInteraction(PlayerInteractAtEntityEvent event) {
        var player = event.getPlayer();
        var item = player.getEquipment().getItem(event.getHand());
        if (event.getPlayer().getGameMode().isInvulnerable()) return;
        if (!hasAbility(item)) return;
        if (!(event.getRightClicked() instanceof Mannequin corpse)) return;
        if (corpse.getVisualFire().equals(TriState.TRUE)) return;
        corpse.setVisualFire(TriState.TRUE);
        var world = player.getWorld();
        world.playSound(
                corpse.getLocation(),
                "minecraft:item.firecharge.use",
                1f,
                1f
        );

        getPlugin().getServer().getScheduler().runTaskLater(
                getPlugin(),
                () -> {
                    if (!corpse.isValid()) {
                        return;
                    }

                    world.playSound(
                            corpse.getLocation(),
                            "minecraft:block.fire.extinguish",
                            1f,
                            1f
                    );
                    world.spawnParticle(
                            Particle.DUST,
                            corpse.getLocation(),
                            20,
                            1,
                            0.3,
                            1,
                            new Particle.DustOptions(Color.fromRGB(38, 18, 17), 1.5f)
                    );
                    corpse.remove();
                },
                getProperty("burn-time", 1) * 20
        );
    }

    @Override
    public ItemStack getDisplayItem() {
        return null;
    }

}
