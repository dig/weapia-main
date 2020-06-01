package net.sunken.core.scoreboard;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.sunken.common.inject.Facet;
import net.sunken.common.player.module.PlayerManager;
import net.sunken.core.player.CorePlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class ScoreboardManager implements Facet {

    @Inject
    private PlayerManager playerManager;

    private Map<String, ScoreboardDetail> playerNames;

    public ScoreboardManager() {
        playerNames = new HashMap<>();
    }

    public void changePlayerName(@NonNull String name, @NonNull String prefix, @NonNull String suffix, @NonNull ChatColor colour) {
        ScoreboardDetail scoreboardDetail = new ScoreboardDetail(prefix, suffix, colour);
        playerNames.put(name, scoreboardDetail);
        playerManager.getOnlinePlayers().forEach(abstractPlayer -> {
            if (abstractPlayer instanceof CorePlayer) {
                CorePlayer corePlayer = (CorePlayer) abstractPlayer;
                ScoreboardWrapper scoreboardWrapper = corePlayer.getScoreboardWrapper();

                if (scoreboardWrapper != null) {
                    Scoreboard scoreboard = scoreboardWrapper.getScoreboard();
                    registerTeam(scoreboard, name, scoreboardDetail);
                }
            }
        });
    }

    public void load(@NonNull Scoreboard scoreboard) {
        for (String name : playerNames.keySet()) {
            ScoreboardDetail scoreboardDetail = playerNames.get(name);
            registerTeam(scoreboard, name, scoreboardDetail);
        }
    }

    private void registerTeam(@NonNull Scoreboard scoreboard, @NonNull String name, @NonNull ScoreboardDetail scoreboardDetail) {
        Team team = scoreboard.getTeam(name);
        if (team == null)
            team = scoreboard.registerNewTeam(name);

        team.setPrefix(scoreboardDetail.getPrefix());
        team.setSuffix(scoreboardDetail.getSuffix());
        team.setColor(scoreboardDetail.getColour());

        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);

        team.addEntry(name);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (playerNames.containsKey(player.getName())) {
            playerNames.remove(player.getName());
            playerManager.getOnlinePlayers().forEach(abstractPlayer -> {
                if (abstractPlayer instanceof CorePlayer) {
                    CorePlayer corePlayer = (CorePlayer) abstractPlayer;
                    ScoreboardWrapper scoreboardWrapper = corePlayer.getScoreboardWrapper();

                    if (scoreboardWrapper != null) {
                        Scoreboard scoreboard = scoreboardWrapper.getScoreboard();
                        Team team = scoreboard.getTeam(player.getName());

                        if (team != null)
                            team.unregister();
                    }
                }
            });
        }
    }

    @Getter
    @AllArgsConstructor
    private class ScoreboardDetail {
        private String prefix;
        private String suffix;
        private ChatColor colour;
    }

}
