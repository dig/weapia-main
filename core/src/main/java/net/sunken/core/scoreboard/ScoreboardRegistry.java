package net.sunken.core.scoreboard;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.sunken.common.player.AbstractPlayer;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log
@Singleton
public class ScoreboardRegistry {

    private final Map<String, CustomScoreboard> scoreboardMap = new HashMap<>();
    private final Map<String, CustomNameDetail> customNames = new HashMap<>();

    public void register(@NonNull String key, @NonNull CustomScoreboard scoreboard) {
        if (!scoreboardMap.containsKey(key)) {
            scoreboardMap.put(key, scoreboard);

            for (String name : customNames.keySet()) {
                CustomNameDetail customNameDetail = customNames.get(name);
                registerCustomName(scoreboard, name, customNameDetail);
            }
        } else {
            log.warning(String.format("ScoreboardRegistry: Trying to register scoreboard when one is already registered. (%)", key));
        }
    }

    public void unregister(@NonNull String key) {
        if (scoreboardMap.containsKey(key)) {
            CustomScoreboard customScoreboard = scoreboardMap.get(key);
            customScoreboard.removeAllEntries();
            scoreboardMap.remove(key);
        }
    }

    public Optional<CustomScoreboard> get(@NonNull String key) {
        return Optional.ofNullable(scoreboardMap.get(key));
    }

    public void changeName(@NonNull String playerName, @NonNull String prefix, @NonNull String suffix, @NonNull ChatColor colour, int order) {
        CustomNameDetail customNameDetail = new CustomNameDetail(prefix, suffix, colour, order);
        customNames.remove(playerName);
        customNames.put(playerName, customNameDetail);
        scoreboardMap.values().forEach(customScoreboard -> registerCustomName(customScoreboard, playerName, customNameDetail));
    }

    public Optional<CustomNameDetail> getCustomName(@NonNull String playerName) {
        return Optional.ofNullable(customNames.get(playerName));
    }

    private void registerCustomName(@NonNull CustomScoreboard customScoreboard, @NonNull String playerName, @NonNull CustomNameDetail customNameDetail) {
        Scoreboard scoreboard = customScoreboard.getScoreboard();

        Team team = scoreboard.getTeam(playerName);
        if (team == null) {
            team = scoreboard.registerNewTeam(playerName);
        }

        Team order = scoreboard.getTeam(customNameDetail.getOrder() + "order");
        if (order == null) {
            order = scoreboard.registerNewTeam(customNameDetail.getOrder() + "order");
        }

        team.setPrefix(customNameDetail.getPrefix());
        team.setSuffix(customNameDetail.getSuffix());
        team.setColor(customNameDetail.getColour());

        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);

        team.addEntry(playerName);
        order.addEntry(playerName);
    }

}
