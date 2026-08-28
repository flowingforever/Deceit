package pro.fazeclan.river.deceit.game;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.role.Faction;
import pro.fazeclan.river.deceit.role.Role;
import pro.fazeclan.river.deceit.util.GameFunctions;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.deceit.util.TimeUtil;
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
import java.util.concurrent.ThreadLocalRandom;
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
        values.setValue("time_limit", config.getLong("deceit.initial-time", 2400));

        ConditionUtil.getWorldConditions(world)
                .getOrCreate(
                        "murder_time_limit",
                        new Condition() {
                            @Override
                            public Function<Condition, String> getHud() {
                                return c -> {
                                    var vl = getGameValues(world.getUID());
                                    long duration = vl.getValue("time_limit", 0L) - vl.getValue("tick", 0L);
                                    return "<red><b>" + TimeUtil.ticksIntoReadableFormat(duration) + "</b></red>";
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

        if (values.getValue("time_limit", 0L) <= values.getValue("tick", 0L)) {
            GameUtil.endGame(world);
        }

        incrementGameTick(world);

    }

    @Override
    public void end(World world, List<Player> players) {

        var gameValues = getGameValues(world.getUID());
        var winners = getWinningRoles(players, gameValues);

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
        }

    }

    private void incrementGameTick(World world) {
        var gameValues = getGameValues(world.getUID());
        gameValues.setValue("tick", gameValues.getValue("tick", 0L) + 1L);
    }

    private long getCurrentGameTick(World world) {
        return getGameValues(world.getUID()).getValue("tick", 0L);
    }

    private void handleRoleSelection(Location spawn, List<Player> players) {
        var values = getGameValues(spawn.getWorld().getUID());

        var innocents = new ArrayList<>(players);
        Collections.shuffle(innocents);

        int hunterCount = (int) (1 + Math.floor(innocents.size() / 8.5));
        int neutralCount = (int) Math.floor(hunterCount / 2.0);

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

    private void selectRoles(List<Player> players, GameValues values, Location spawn, Faction faction) {
        var manager = plugin.getRoleManager();

        var limitedRoles = new ArrayList<Role>();
        for (var role : manager.getLimitedRoles(faction)) {
            for (int i = 0; i < role.getLimit(); i++) {
                limitedRoles.add(role);
            }
        }
        Collections.shuffle(limitedRoles);

        var unlimitedRoles = new ArrayList<>(manager.getUnlimitedRoles(faction));
        Collections.shuffle(unlimitedRoles);
        int size = unlimitedRoles.size();
        int index = 0;

        float dividend = (float) plugin.getConfig().getDouble("role-chance-dividend", 2.0);
        float chance = 1.0f;
        for (var player : players) {
            if (chance > 0f) {
                if (ThreadLocalRandom.current().nextFloat() <= chance && !limitedRoles.isEmpty()) {
                    GameFunctions.addPlayer(player, limitedRoles.getFirst(), values, spawn);
                    limitedRoles.removeFirst();
                    chance /= dividend;
                    continue;
                }

                chance = 0f;
            }

            if (index + 1 > size) {
                index = 0;
            }

            GameFunctions.addPlayer(player, unlimitedRoles.get(index), values, spawn);
            index++;
        }
    }

    private List<Role> getMainRoles(List<Player> players, GameValues values) {
        var list = new ArrayList<Role>();
        for (var player : players) {
            var role = RoleUtil.getRole(player, values);
            if (list.stream().noneMatch(r -> r.isSameTeam(role))) {
                if (role.isTakesPriority()) {
                    list.addFirst(role);
                } else {
                    list.addLast(role);
                }
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
