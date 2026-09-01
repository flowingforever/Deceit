package pro.fazeclan.river.deceit.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.event.MurderInitEvent;
import pro.fazeclan.river.deceit.event.MurderTickEvent;
import pro.fazeclan.river.deceit.role.Faction;
import pro.fazeclan.river.deceit.role.Role;
import pro.fazeclan.river.deceit.util.GameFunctions;
import pro.fazeclan.river.deceit.util.GlowUtil;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.deceit.util.TimeUtil;
import pro.fazeclan.river.jarona.Jarona;
import pro.fazeclan.river.jarona.condition.Condition;
import pro.fazeclan.river.jarona.game.GameValues;
import pro.fazeclan.river.jarona.game.GameWithMap;
import pro.fazeclan.river.jarona.util.ConditionUtil;
import pro.fazeclan.river.jarona.util.GameUtil;
import pro.fazeclan.river.jarona.util.WorldlessLocation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

// say that again...
public class DeceitMurderGame extends GameWithMap {

    private final Deceit plugin;

    public DeceitMurderGame(Deceit plugin) {
        super(
                "Deceit: Murder",
                Deceit.getKey("murder"),
                2
        );
        this.plugin = plugin;
    }

    @Override
    public void init(World world, List<Player> players) {

        var config = YamlConfiguration.loadConfiguration(new File(world.getWorldFolder(), "map_config.yml"));

        var spawn = WorldlessLocation.deserialize("spawn", config).toLocation(world);
        handleRoleSelection(spawn, players);

        world.setGameRule(GameRules.LOCATOR_BAR, false);
        world.setGameRule(GameRules.REDUCED_DEBUG_INFO, true);
        world.setGameRule(GameRules.FALL_DAMAGE, true);

        // announcement text
        var values = getGameValues(world.getUID());
        var scheduler = Bukkit.getScheduler();
        var mm = MiniMessage.miniMessage();
        for (var player : players) {
            Role role = values.getValue("role_" + player.getUniqueId());
            scheduler.runTaskLater(plugin, () -> {
                var sound = role.getAnnouncementSound();
                player.showTitle(Title.title(
                        mm.deserialize("<gray><< " + role.getPrefix() + " >></gray>"),
                        mm.deserialize(role.getAnnouncement())
                ));
                player.playSound(
                        player,
                        sound.name().asString(),
                        sound.volume(),
                        sound.pitch()
                );
            }, 60);

            long delay = 120;
            for (var text : role.getDescription()) {
                scheduler.runTaskLater(plugin, () -> {
                    player.showTitle(Title.title(
                            mm.deserialize("<gray><< " + role.getPrefix() + " >></gray>"),
                            mm.deserialize(text),
                            0, 65, 20
                    ));
                    player.playSound(
                            player.getLocation(),
                            "minecraft:block.note_block.bit",
                            SoundCategory.PLAYERS,
                            1f,
                            1f
                    );
                }, delay);
                delay += 60;
            }
        }

        // timer that only shows for the non-innocent
        values.setValue("time_limit", config.getLong("deceit.initial-time", 4800));

        // coin handout clock
        var handout = config.getLong("deceit.bell_handout", 2400);
        values.setValue("bell_handout", handout);
        values.setValue("initial_bell_handout", handout);
        values.setValue("bell_handout_count", 0);

        var worldConditions = ConditionUtil.getWorldConditions(world);
        worldConditions.getOrCreate(
                "murder_time_limit",
                new Condition() {
                    @Override
                    public Function<Condition, String> getHud() {
                        return c -> {
                            var vl = getGameValues(world.getUID());
                            long duration = vl.getValue("time_limit", 4800L) - vl.getValue("tick", 0L);
                            return "<red>\uD83D\uDDE1 <b>" + TimeUtil.ticksIntoReadableFormat(duration) + "</b></red>";
                        };
                    }

                    @Override
                    public BiFunction<Condition, Player, Boolean> getHudCondition() {
                        return (c, v) -> {
                            var vl = getGameValues(world.getUID());
                            return RoleUtil.isEvil(v, vl) || v.getGameMode().isInvulnerable();
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

        worldConditions.getOrCreate(
                "murder_bell_handout",
                new Condition() {
                    @Override
                    public Function<Condition, String> getHud() {
                        return c -> {
                            var vl = getGameValues(world.getUID());
                            long duration = vl.getValue("bell_handout", 2400L) - vl.getValue("tick", 0L);
                            return "<yellow>\uD83D\uDD14 <b>" + TimeUtil.ticksIntoReadableFormat(duration) + "</b></yellow>";
                        };
                    }

                    @Override
                    public BiFunction<Condition, Player, Boolean> getHudCondition() {
                        return (_, _) -> true;
                    }

                    @Override
                    public boolean getAvailable() {
                        return true;
                    }

                    @Override
                    public void reset() {}
                }
        );

        plugin.getServer().getPluginManager().callEvent(new MurderInitEvent(players, world, this));
    }

    @Override
    public void tick(World world, List<Player> players) {

        var values = getGameValues(world.getUID());
        var winners = getWinningRoles(players, values);

        for (var winner : winners) {
            if (winner.winningEndsGames()) {
                GameUtil.endGame(world);
                return;
            }
        }

        if (values.getValue("bell_handout", 2400L) <= getCurrentGameTick(world)) {
            for (var player : players) {
                player.sendMessage(Component.text(" ! ").decorate(TextDecoration.BOLD).color(NamedTextColor.YELLOW)
                                .append(Component.text("All players have received a bell handout!").decoration(TextDecoration.BOLD, false).color(NamedTextColor.WHITE))
                                .append(Component.text(" (+2 \uD83D\uDD14)").color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, false))
                );
                GameFunctions.payout(player, values);
            }
            values.setValue("bell_handout", values.getValue("initial_bell_handout", 1800L) * (values.getValue("bell_handout_count", 0) + 2L));
            values.setValue("bell_handout_count", values.getValue("bell_handout_count", 0) + 1);
        }

        if (values.getValue("time_limit", 4800L) <= getCurrentGameTick(world)) {
            GameUtil.endGame(world);
        }

        plugin.getServer().getPluginManager().callEvent(new MurderTickEvent(players, world, this));

        incrementGameTick(world);

    }

    @Override
    public void end(World world, List<Player> players) {

        var gameValues = getGameValues(world.getUID());
        var winners = getWinningRoles(players, gameValues);
        var svc = Jarona.getInstance().getVoicechatPlugin();

        // end of game title builders
        var title = new StringBuilder();
        var subtitle = new StringBuilder();

        title.append("<gray><< ");
        for (var winner : winners) {
            if (!title.toString().equals("<gray><< ")) {
                title.append(" + ");
            }
            title.append(winner.getPrefix());

            if (!subtitle.isEmpty()) {
                subtitle.append(" + ");
            }
            subtitle.append(winner.getName()).append("s");
        }

        // if nothing changed, just do none bro
        if (title.toString().equals("<gray><< ")) {
            title.append("<gray>?</gray>");
        }
        title.append(" >></gray>");

        if (subtitle.isEmpty()) {
            subtitle.append("No one");
        }
        subtitle.append(" won!");

        var mm = MiniMessage.miniMessage();
        for (var player : players) {
            player.showTitle(Title.title(
                    mm.deserialize(title.toString()),
                    mm.deserialize(subtitle.toString())
            ));
            player.setGlowing(false);
            GlowUtil.removeGlowOfPlayerToWorld(player.getWorld(), player);

            if (svc != null) {
                svc.removePlayer(player);
            }
        }

    }

    private void incrementGameTick(World world) {
        var gameValues = getGameValues(world.getUID());
        gameValues.setValue("tick", getCurrentGameTick(world) + 1L);
    }

    private long getCurrentGameTick(World world) {
        return getGameValues(world.getUID()).getValue("tick", 0L);
    }

    private void handleRoleSelection(Location spawn, List<Player> players) {
        var values = getGameValues(spawn.getWorld().getUID());

        var innocents = new ArrayList<>(players);
        Collections.shuffle(innocents);

        // making sure to keep these b4 anything else
        // so percentage conditions aren't messed up
        // by the innocent count "technically" dropping
        int hunterCount = getAmountForFaction(players, Faction.TRAITOR);
        int neutralCount = getAmountForFaction(players, Faction.NEUTRAL);

        var traitors = new ArrayList<Player>();
        for (int i = 0; i < hunterCount; i++) {
            traitors.add(innocents.removeLast());
        }

        selectRoles(traitors, values, spawn, Faction.TRAITOR);

        var neutrals = new ArrayList<Player>();
        for (int i = 0; i < neutralCount; i++) {
            neutrals.add(innocents.removeLast());
        }

        selectRoles(neutrals, values, spawn, Faction.NEUTRAL);
        selectRoles(innocents, values, spawn, Faction.INNOCENT);
    }

    private int getAmountForFaction(List<Player> players, Faction faction) {
        var config = plugin.getConfig();
        return (int) Math.min(
                Math.floor(
                        ((config.getDouble("faction." + faction.toString().toLowerCase() + ".percentage", 11.7) / 100.0) * players.size())
                        + config.getInt("faction." + faction.toString().toLowerCase() + ".minimum")
                ),
                players.size()
        );
    }

    private void selectRoles(List<Player> players, GameValues values, Location spawn, Faction faction) {
        var manager = plugin.getRoleManager();

        var iterablePlayers = new ArrayList<>(players);

        // go through the special roles first
        var limitedRoles = new ArrayList<>(manager.getLimitedRoles(faction));
        Collections.shuffle(limitedRoles);
        for (var role : limitedRoles) {
            int count = getAmountOfRole(players, role);
            for (int i = 0; i < count; i++) {
                if (iterablePlayers.isEmpty()) {
                    break;
                }
                GameFunctions.addPlayer(
                        iterablePlayers.removeLast(),
                        role,
                        values,
                        spawn
                );
            }
        }

        // set base roles
        if (!iterablePlayers.isEmpty()) {
            // tries to make an even split in case there are multiple roles that don't need selection in this particular faction
            var unlimitedRoles = new ArrayList<>(manager.getUnlimitedRoles(faction));
            Collections.shuffle(unlimitedRoles);
            int size = unlimitedRoles.size();
            int index = 0;

            for (var player : iterablePlayers) {
                if (index + 1 > size) {
                    index = 0;
                }

                GameFunctions.addPlayer(player, unlimitedRoles.get(index), values, spawn);
                index++;
            }
        }
    }

    private int getAmountOfRole(List<Player> players, Role role) {
        return (int) Math.min(
                Math.floor(
                        ((role.getSelectionPercentage() / 100.0) * players.size())
                        + role.getMinimumCount()
                ),
                players.size()
        );
    }

    private List<Role> getMainRoles(List<Player> players, GameValues values) {
        var list = new ArrayList<Role>();
        for (var player : players) {
            var role = RoleUtil.getRole(player, values);
            if (list.stream().noneMatch(r -> r.isSameTeam(role))) {
                if (role.isTakesPriority()) {
                    if (role.winningEndsGames()) {
                        return List.of(role);
                    }
                }

                list.addLast(role);
            }
        }
        return list;
    }

    private List<Role> getWinningRoles(List<Player> players, GameValues values) {
        var manager = plugin.getRoleManager();
        return getMainRoles(players, values)
                .stream()
                .filter(role -> role.hasWon(players, values))
                .map(role -> manager.getRole(role.winsWith()))
                .filter(Objects::nonNull)
                .toList();
    }

}
