package pro.fazeclan.river.deceit.role.definitions.neutral;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.event.MurderEliminationEvent;
import pro.fazeclan.river.deceit.event.MurderInitEvent;
import pro.fazeclan.river.deceit.event.MurderTickEvent;
import pro.fazeclan.river.deceit.menu.ShopEntry;
import pro.fazeclan.river.deceit.role.definitions.AbstractNeutralRole;
import pro.fazeclan.river.deceit.util.GlowUtil;
import pro.fazeclan.river.deceit.util.MessageUtil;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.deceit.util.TimeUtil;
import pro.fazeclan.river.jarona.condition.Condition;
import pro.fazeclan.river.jarona.game.GameValues;
import pro.fazeclan.river.jarona.util.ConditionUtil;
import pro.fazeclan.river.jarona.util.GameUtil;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FiendRole extends AbstractNeutralRole {
    public FiendRole(Deceit plugin) {
        super(plugin, "fiend");
    }

    @Override
    public List<ItemStack> getSpawnItems() {
        return List.of(
                ItemType.BOW.createItemStack(meta -> meta.setUnbreakable(true)),
                ItemType.ARROW.createItemStack(20),
                ItemType.WOODEN_SWORD.createItemStack(meta -> meta.setUnbreakable(true))
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
                )
        );
    }

    @Override
    public ItemStack getDisplayItem() {
        return ItemType.WOODEN_SWORD.createItemStack();
    }

    @Override
    public boolean hasWon(List<Player> players, GameValues values) {
        var manager = getPlugin().getRoleManager();
        var innocent = manager.getRole("innocent");
        var traitor = manager.getRole("traitor");
        return (innocent.hasWon(players, values) || traitor.hasWon(players, values))
                && RoleUtil.isTeamAlive(players, values, winsWith());
    }

    @EventHandler
    private void onGameStart(MurderInitEvent event) {
        var world = event.getWorld();
        var game = event.getGame();
        var values = game.getGameValues(world.getUID());

        values.setValue("initial_fiend_timer", getProperty("timer", 2400).longValue());
        values.setValue("fiend_timer", values.getValue("initial_fiend_timer", 2400L));
        ConditionUtil.getWorldConditions(world)
                .getOrCreate(
                        "fiend_timer",
                        new Condition() {
                            @Override
                            public Function<Condition, String> getHud() {
                                return c -> {
                                    var vl = game.getGameValues(world.getUID());
                                    if (vl.getValue("fiend_locked", false)) {
                                        return "<dark_purple>☄ Survive.</dark_purple>";
                                    }

                                    long duration = Math.max(0, vl.getValue("fiend_timer", 4800L));
                                    String completed = "☒";
                                    if (vl.getValue("fiend_got_kill", false)) {
                                        completed = "☑";
                                    }

                                    return "<dark_purple>☄ <b>" + TimeUtil.ticksIntoReadableFormat(duration) + "</b> " + completed + "</dark_purple>";
                                };
                            }

                            @Override
                            public BiFunction<Condition, Player, Boolean> getHudCondition() {
                                return (c, v) -> {
                                    var vl = game.getGameValues(world.getUID());
                                    var fiend = getPlugin().getRoleManager().getRole("fiend");
                                    return fiend.equals(RoleUtil.getRole(v, vl)) || v.getGameMode().isInvulnerable();
                                };
                            }

                            @Override
                            public boolean getAvailable() {
                                return true;
                            }

                            @Override
                            public void reset() {}
                        }
                );
    }

    @EventHandler
    private void onGameTick(MurderTickEvent event) {
        var world = event.getWorld();
        var game = event.getGame();
        var values = game.getGameValues(world.getUID());

        var fiends = RoleUtil.getAllWithTeam(world, values, winsWith());
        if (fiends.isEmpty()) return;
        values.setValue("fiend_timer", values.getValue("fiend_timer", 2400L) - 1);
        if (values.getValue("fiend_locked", false)) return;
        if (values.getValue("fiend_timer", 2400L) > 0) return;
        if (values.getValue("fiend_got_kill", false)) {
            values.setValue("fiend_timer", values.getValue("initial_fiend_timer", 2400L));
            values.setValue("fiend_got_kill", false);
            for (var fiend : RoleUtil.getAllWithTeam(world, values, winsWith())) {
                var mm = MiniMessage.miniMessage();
                fiend.sendMessage(mm.deserialize(
                        getPrefix() + " The task has started up again. Eliminate a player soon."
                ));
            }
        } else {
            values.setValue("fiend_locked", true);
            for (var fiend : fiends) {
                GlowUtil.setGlowOfPlayerToWorld(fiend.getWorld(), fiend, NamedTextColor.DARK_PURPLE);
                fiend.setGlowing(true);
            }
            var mm = MiniMessage.miniMessage();
            world.getPlayers().forEach(p -> {
                p.sendMessage(mm.deserialize(getPrefix() + " The fiends have <red>failed their task</red>. They are now revealed until death or game end."));
                p.playSound(
                        p.getLocation(),
                        "minecraft:block.respawn_anchor.deplete",
                        1f,
                        1f
                );
            });
        }
    }

    @EventHandler
    private void onEliminationOfFiend(MurderEliminationEvent event) {
        var eliminated = event.getEliminated();
        var values = GameUtil.getGame(eliminated).getGameValues(eliminated.getWorld().getUID());
        var role = RoleUtil.getRole(eliminated, values);
        if (role == null) return;
        if (!role.equals(this)) return;
        if (!values.getValue("fiend_locked", false)) return;
        GlowUtil.removeGlowOfPlayerToWorld(eliminated.getWorld(), eliminated);
    }

    @EventHandler
    private void onFiendElimination(MurderEliminationEvent event) {
        var eliminated = event.getEliminated();
        var source = event.getSource();
        if (!(source.getCausingEntity() instanceof Player attacker)) return;
        var world = eliminated.getWorld();
        var values = GameUtil.getGame(world).getGameValues(world.getUID());
        var attackerRole = RoleUtil.getRole(attacker, values);
        if (attackerRole == null) return;
        if (!attackerRole.winsWith().equals(winsWith())) return;
        var victimRole = RoleUtil.getRole(eliminated, values);
        if (victimRole == null) return;
        if (victimRole.winsWith().equals(winsWith())) return;
        if (values.getValue("fiend_locked", false)) return;

        values.setValue("fiend_got_kill", true);
        var mm = MiniMessage.miniMessage();
        for (var fiend : RoleUtil.getAllWithTeam(world, values, winsWith())) {
            fiend.sendMessage(mm.deserialize(
                    getPrefix() + " " + attacker.getName() + " has completed the task by eliminating " + eliminated.getName() + "!"
            ));
        }
    }

    @Override
    public String getPrefix() {
        return "<dark_purple>☄</dark_purple>";
    }
}
