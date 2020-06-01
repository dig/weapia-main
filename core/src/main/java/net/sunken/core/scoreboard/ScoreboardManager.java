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

    private Map<UUID, ScoreboardDetail> playerNames;

    public ScoreboardManager() {
        playerNames = new HashMap<>();
    }

    public void changePlayerName(@NonNull UUID uuid, @NonNull String prefix, @NonNull String suffix, @NonNull ChatColor colour) {
        ScoreboardDetail scoreboardDetail = new ScoreboardDetail(prefix, suffix, colour);
        playerNames.put(uuid, scoreboardDetail);
        playerManager.getOnlinePlayers().forEach(abstractPlayer -> {
            if (abstractPlayer instanceof CorePlayer) {
                CorePlayer corePlayer = (CorePlayer) abstractPlayer;
                ScoreboardWrapper scoreboardWrapper = corePlayer.getScoreboardWrapper();

                if (scoreboardWrapper != null) {
                    Scoreboard scoreboard = scoreboardWrapper.getScoreboard();
                    registerTeam(scoreboard, uuid, scoreboardDetail);
                }
            }
        });
    }

    public void load(@NonNull Scoreboard scoreboard) {
        for (UUID uuid : playerNames.keySet()) {
            ScoreboardDetail scoreboardDetail = playerNames.get(uuid);
            registerTeam(scoreboard, uuid, scoreboardDetail);
        }
    }

    private void registerTeam(@NonNull Scoreboard scoreboard, @NonNull UUID uuid, @NonNull ScoreboardDetail scoreboardDetail) {
        Team team = scoreboard.getTeam(uuid.toString());
        if (team == null)
            team = scoreboard.registerNewTeam(uuid.toString());

        team.setPrefix(scoreboardDetail.getPrefix());
        team.setSuffix(scoreboardDetail.getSuffix());
        team.setColor(scoreboardDetail.getColour());

        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);

        team.addEntry(uuid.toString());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (playerNames.containsKey(player.getUniqueId())) {
            playerNames.remove(player.getUniqueId());

            playerManager.getOnlinePlayers().forEach(abstractPlayer -> {
                if (abstractPlayer instanceof CorePlayer) {
                    CorePlayer corePlayer = (CorePlayer) abstractPlayer;
                    ScoreboardWrapper scoreboardWrapper = corePlayer.getScoreboardWrapper();

                    if (scoreboardWrapper != null) {
                        Scoreboard scoreboard = scoreboardWrapper.getScoreboard();
                        Team team = scoreboard.getTeam(player.getUniqueId().toString());

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
