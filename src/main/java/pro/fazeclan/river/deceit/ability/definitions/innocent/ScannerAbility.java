package pro.fazeclan.river.deceit.ability.definitions.innocent;

import io.papermc.paper.event.entity.EntityCollideWithEntityEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.SulfurCube;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.ability.Ability;
import pro.fazeclan.river.deceit.ability.AbilityEvent;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.jarona.util.GameUtil;

import java.util.concurrent.atomic.AtomicBoolean;

public class ScannerAbility extends Ability {

    public ScannerAbility(Deceit plugin) {
        super(plugin, "scanner");
    }

    @EventHandler
    private void onScannerCollide(EntityCollideWithEntityEvent event) {
        if (event.getEntities().stream().anyMatch(entity -> entity.getScoreboardTags().contains("no_collide"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onScannerUse(AbilityEvent event) {
        if (!event.getExpectedAbility().equals(getId())) return;
        var player = event.getPlayer();
        var item = event.getItemStack();
        var values = GameUtil.getGame(player).getGameValues(player.getWorld().getUID());

        item.setAmount(item.getAmount() - 1);
        player.setCooldown(item, getProperty("cooldown", 0));

        var world = player.getWorld();
        world.spawn(player.getLocation().clone().setRotation(0,0), SulfurCube.class, sc -> {
            sc.getEquipment().setItem(EquipmentSlot.BODY, ItemType.LIGHTNING_ROD.createItemStack());
            sc.setInvisible(true);
            sc.setSilent(true);
            sc.setAware(false);
            sc.getScoreboardTags().add("no_collide");

            var scheduler = getPlugin().getServer().getScheduler();
            scheduler.runTaskLater(
                    getPlugin(),
                    () -> {
                        if (!sc.isValid()) {
                            return;
                        }

                        double radius = getProperty("radius", 3.0);
                        var nearbyPlayers = sc.getNearbyEntities(radius, radius, radius)
                                .stream()
                                .filter(entity -> entity instanceof Player)
                                .map(entity -> (Player) entity)
                                .filter(p -> !p.equals(player))
                                .toList();
                        AtomicBoolean hasTraitor = new AtomicBoolean(false);
                        world.strikeLightningEffect(sc.getLocation());
                        int tick = 1;
                        for (var p : nearbyPlayers) {
                            scheduler.runTaskLater(
                                    getPlugin(),
                                    () -> {
                                        world.strikeLightningEffect(p.getLocation());
                                        if (RoleUtil.isTraitor(p, values)) {
                                            hasTraitor.set(true);
                                        }
                                    },
                                    tick++
                            );
                        }

                        scheduler.runTaskLater(
                                getPlugin(),
                                () -> {
                                    world.spawn(sc.getLocation(), TextDisplay.class, td -> {
                                        if (hasTraitor.get()) {
                                            td.text(Component.text("Traitor Detected").color(NamedTextColor.RED));
                                        } else {
                                            td.text(Component.text("No Traitors Found").color(NamedTextColor.GREEN));
                                        }
                                        td.setTransformation(new Transformation(
                                                new Vector3f(0f, 0.5f, 0f),
                                                new Quaternionf(),
                                                new Vector3f(0.75f, 0.75f, 0.75f),
                                                new Quaternionf()
                                        ));
                                        td.setBillboard(Display.Billboard.VERTICAL);
                                        sc.addPassenger(td);
                                    });
                                },
                                tick
                        );

                    },
                    getProperty("delay", 5)
            );
        });
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.LIGHTNING_ROD.createItemStack();
    }

}
