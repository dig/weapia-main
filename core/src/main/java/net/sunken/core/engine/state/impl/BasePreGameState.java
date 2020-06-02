package net.sunken.core.engine.state.impl;

import net.sunken.common.server.Game;
import net.sunken.core.Constants;
import net.sunken.core.player.CorePlayer;
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
    }

    @Override
    public void onDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
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
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 2F));
            } else if (timeDiff >= 3000 && timeDiff <= 4000) {
                Bukkit.broadcastMessage(String.format(Constants.COUNTDOWN_GAME_SECONDS, 3));
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 2F));
            } else if (timeDiff >= 2000 && timeDiff < 3000) {
                Bukkit.broadcastMessage(String.format(Constants.COUNTDOWN_GAME_SECONDS, 2));
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 2F));
            } else if (timeDiff >= 1000 && timeDiff < 2000) {
                Bukkit.broadcastMessage(String.format(Constants.COUNTDOWN_GAME_SECONDS, 1));
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 2F));
            }
        }
    }

}
