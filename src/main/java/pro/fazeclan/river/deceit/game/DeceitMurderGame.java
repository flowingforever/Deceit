package pro.fazeclan.river.deceit.game;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.role.Faction;
import pro.fazeclan.river.deceit.role.Role;
import pro.fazeclan.river.deceit.util.GameFunctions;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.jarona.game.GameValues;
import pro.fazeclan.river.jarona.game.GameWithMap;
import pro.fazeclan.river.jarona.util.GameUtil;
import pro.fazeclan.river.jarona.util.WorldlessLocation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

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

    }

    @Override
    public void tick(World world, List<Player> players) {

        var gameValues = getGameValues(world.getUID());
        var winners = getWinningRoles(players, gameValues);

        for (var winner : winners) {
            if (winner.winningEndsGames()) {
                GameUtil.endGame(world);
                return;
            }
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
