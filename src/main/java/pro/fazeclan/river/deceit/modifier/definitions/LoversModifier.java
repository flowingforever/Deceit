package pro.fazeclan.river.deceit.modifier.definitions;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import pro.fazeclan.river.deceit.Deceit;
import pro.fazeclan.river.deceit.modifier.Modifier;
import pro.fazeclan.river.deceit.util.GameFunctions;
import pro.fazeclan.river.deceit.util.MurderWinner;
import pro.fazeclan.river.deceit.util.RoleUtil;
import pro.fazeclan.river.jarona.game.GameValues;
import pro.fazeclan.river.jarona.tablist.NameContext;
import pro.fazeclan.river.jarona.util.NametagUtil;
import pro.fazeclan.river.jarona.util.NicknameUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class LoversModifier extends Modifier implements MurderWinner {

    public LoversModifier(Deceit plugin) {
        super(plugin, "lovers");
    }

    @Override
    public String getPrefix() {
        return "<" + getMiniMessageColor() + ">❤</" + getMiniMessageColor() + ">";
    }

    @Override
    public String getMiniMessageColor() {
        return getProperty("color", "white");
    }

    @Override
    public String getName() {
        return getProperty("name", "Lover");
    }

    @Override
    public boolean hasWon(List<Player> players, GameValues values) {
        return players.stream()
                .filter(p -> !p.getGameMode().isInvulnerable())
                .allMatch(p -> {
                    var uuid = values.getValue("lovers_" + p.getUniqueId(), UUID.class);
                    if (uuid == null) return false;
                    var partner = Bukkit.getPlayer(uuid);
                    return partner != null && !partner.getGameMode().isInvulnerable();
                });
    }

    @Override
    public String winsWith() {
        return "lovers";
    }

    @Override
    public boolean winningEndsGames() {
        return true;
    }

    @Override
    public void init(List<Player> players, World world, GameValues values) {
        List<Player> loveCandidates = new ArrayList<>(players);
        Collections.shuffle(loveCandidates);
        var candidateOne = loveCandidates.removeFirst();
        Player candidateTwo;

        // ensure it may only be an evil-innocent, innocent-innocent, or evil-neutral combo
        do {
            candidateTwo = loveCandidates.removeFirst();
        } while (candidateTwo == null || RoleUtil.isTraitor(candidateTwo, values));

        var mm = MiniMessage.miniMessage();

        var roleOne = RoleUtil.getRole(candidateOne, values);
        var roleTwo = RoleUtil.getRole(candidateTwo, values);

        values.setValue("lovers_" + candidateOne.getUniqueId(), candidateTwo.getUniqueId());
        Player finalCandidateTwo = candidateTwo;
        NametagUtil.setName(candidateOne, values, (t, v, ctx, vl) -> {
            if (RoleUtil.canSeeTeam(v, t, values)
                    || vl.getValue("revealed_" + candidateOne.getUniqueId(), false)) {
                if (ctx.equals(NameContext.TABLIST)) {
                    return roleOne.getPrefix() + " %jarona_nickname%";
                } else {
                    var color = roleOne.getMiniMessageColor();
                    return roleOne.getPrefix() + " <" + color + ">" + roleOne.getName() + "<newline>%jarona_nickname%";
                }
            }

            if (v.equals(finalCandidateTwo)) {
                if (ctx.equals(NameContext.TABLIST)) {
                    return roleOne.getPrefix() + " " + getPrefix() + " %jarona_nickname%";
                } else {
                    var color = roleOne.getMiniMessageColor();
                    return roleOne.getPrefix() + " <" + color + ">" + roleOne.getName() + " " + getPrefix() + "<newline>%jarona_nickname%";
                }
            }

            return "%jarona_nickname%";
        });
        candidateOne.sendMessage(mm.deserialize(
                getPrefix() + " You are lovers with " + NicknameUtil.getNickname(candidateTwo) + "!"
        ));


        values.setValue("lovers_" + candidateTwo.getUniqueId(), candidateOne.getUniqueId());
        NametagUtil.setName(candidateTwo, values, (t, v, ctx, vl) -> {
            if (RoleUtil.canSeeTeam(v, t, values)
                    || vl.getValue("revealed_" + finalCandidateTwo.getUniqueId(), false)) {
                if (ctx.equals(NameContext.TABLIST)) {
                    return roleTwo.getPrefix() + " %jarona_nickname%";
                } else {
                    var color = roleTwo.getMiniMessageColor();
                    return roleTwo.getPrefix() + " <" + color + ">" + roleTwo.getName() + "<newline>%jarona_nickname%";
                }
            }

            if (v.equals(candidateOne)) {
                if (ctx.equals(NameContext.TABLIST)) {
                    return roleTwo.getPrefix() + " " + getPrefix() + " %jarona_nickname%";
                } else {
                    var color = roleTwo.getMiniMessageColor();
                    return roleTwo.getPrefix() + " <" + color + ">" + roleTwo.getName() + " " + getPrefix() + "<newline>%jarona_nickname%";
                }
            }

            return "%jarona_nickname%";
        });
        candidateTwo.sendMessage(mm.deserialize(
                getPrefix() + " You are lovers with " + NicknameUtil.getNickname(candidateOne) + "!"
        ));
    }

    @Override
    public void tick(List<Player> players, World world, GameValues values) {
        var lovers = players.stream()
                .filter(p -> !p.getGameMode().isInvulnerable())
                .filter(p -> values.getValue("lovers_" + p.getUniqueId()) != null)
                .toList();
        if (lovers.size() < 2) {
            for (var player : lovers) {
                GameFunctions.eliminatePlayer(player, false, false);
            }
        }
    }
}
