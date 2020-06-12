package net.sunken.core.engine.state.impl;

import com.google.inject.Inject;
import net.sunken.common.util.AsyncHelper;
import net.sunken.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public abstract class BasePostGameState extends EventGameState {

    @Inject
    private Core core;

    protected long gameEndTimeMillis;

    @Override
    public void start(BaseGameState previous) {
        gameEndTimeMillis = System.currentTimeMillis() + (30 * 1000);
    }

    @Override
    public void stop(BaseGameState next) {
    }

    @Override
    public void tick(int tickCount) {
        if (gameEndTimeMillis > 0 && gameEndTimeMillis <= System.currentTimeMillis()) {
            gameEndTimeMillis = 0;
            Bukkit.shutdown();
        }
    }

    @Override
    public void onJoin(Player player) {
    }

    @Override
    public void onQuit(Player player) {
    }

    @Override
    public void onDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
    }

    @Override
    public boolean canBreak(Player player, Block block) {
        return false;
    }

    @Override
    public boolean canPlace(Player player, Block block) {
        return false;
    }

    @Override
    public boolean canTakeEntityDamage(Player target, Entity instigator, EntityDamageEvent.DamageCause damageCause) {
        return false;
    }

    @Override
    public boolean canDealEntityDamage(Player instigator, Entity target, EntityDamageEvent.DamageCause damageCause) {
        return false;
    }

    @Override
    public boolean canTakeDamage(Player instigator, double finalDamage, double damage) {
        return false;
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
            event.getWorld().setWeatherDuration(0);
            event.getWorld().setThundering(false);
        }
    }

}
