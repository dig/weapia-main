package net.sunken.core.engine.state.impl;

import net.sunken.common.server.Game;
import net.sunken.core.Constants;
import net.sunken.core.engine.state.PlayerSpectatorState;
import net.sunken.core.player.CorePlayer;
import net.sunken.core.scoreboard.ScoreboardEntry;
import net.sunken.core.scoreboard.ScoreboardWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.concurrent.TimeUnit;

public abstract class BasePreGameState extends EventGameState {

    protected BaseGameState gameState;
    protected long gameStartTimeMillis;

    @Override
    public void start(BaseGameState previous) {
        gameStartTimeMillis = System.currentTimeMillis() + (20 * 1000);
    }

    @Override
    public void onJoin(Player player) {
        setState(player.getUniqueId(), new PlayerSpectatorState(player));
    }

    @Override
    public void onDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
        setState(event.getEntity().getUniqueId(), new PlayerSpectatorState(event.getEntity()));
    }

    @Override
    public void tick(int tickCount) {
        if (gameStartTimeMillis > 0 && gameStartTimeMillis <= System.currentTimeMillis()) {
            gameStartTimeMillis = 0;
            engineManager.setState(gameState);
        }

        //--- Countdown
        if (tickCount % 20 == 0) {
            long timeDiff = gameStartTimeMillis - System.currentTimeMillis();

            if (timeDiff >= 10000 && timeDiff <= 11000) {
                Bukkit.broadcastMessage(String.format(Constants.COUNTDOWN_GAME_SECONDS, 10));
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 0.5F, 2F));
            } else if (timeDiff >= 3000 && timeDiff <= 4000) {
                Bukkit.broadcastMessage(String.format(Constants.COUNTDOWN_GAME_SECONDS, 3));
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 0.5F, 2F));
            } else if (timeDiff >= 2000 && timeDiff < 3000) {
                Bukkit.broadcastMessage(String.format(Constants.COUNTDOWN_GAME_SECONDS, 2));
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 0.5F, 2F));
            } else if (timeDiff >= 1000 && timeDiff < 2000) {
                Bukkit.broadcastMessage(String.format(Constants.COUNTDOWN_GAME_SECONDS, 1));
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 0.5F, 2F));
            }
        }

        //--- Scoreboard
        if (tickCount % 20 == 0) {
            long playingCount = getPlayingCount();
            playerManager.getOnlinePlayers().forEach(abstractPlayer -> {
                CorePlayer corePlayer = (CorePlayer) abstractPlayer;
                ScoreboardWrapper scoreboardWrapper = corePlayer.getScoreboardWrapper();
                Game game = pluginInform.getServer().getGame();

                long timeDiff = (gameStartTimeMillis > System.currentTimeMillis() ? gameStartTimeMillis - System.currentTimeMillis() : 0);

                //--- Players
                ScoreboardEntry playersEntry = scoreboardWrapper.getEntry("PlayersValue");
                if (playersEntry != null) playersEntry.update(ChatColor.GREEN + " " + playingCount + "/" + game.getMaxPlayers());

                //--- Time
                ScoreboardEntry timeEntry = scoreboardWrapper.getEntry("TimeValue");
                if (timeEntry != null) timeEntry.update(ChatColor.LIGHT_PURPLE + " " + String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(timeDiff), TimeUnit.MILLISECONDS.toSeconds(timeDiff) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDiff))));
            });
        }
    }

}
