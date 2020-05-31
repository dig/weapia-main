package net.sunken.core.engine;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.sunken.common.event.EventManager;
import net.sunken.common.inject.Facet;
import net.sunken.core.engine.exception.*;
import net.sunken.core.engine.state.impl.BaseGameState;
import net.sunken.core.engine.state.impl.EventGameState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

@Log
@Getter
@Singleton
public class EngineManager implements Facet, Listener {

    @Inject
    private JavaPlugin plugin;
    @Inject
    private EventManager eventManager;

    @Getter
    private GameMode gameMode;
    @Getter
    private BaseGameState currentGameState;
    private int tickCount = 1;

    public void setGameMode(@NonNull GameMode gameMode) throws GameModeAlreadySetException {
        if (this.gameMode == null) {
            this.gameMode = gameMode;

            //--- Set state
            this.setState(this.gameMode.getInitialState().get());

            //--- State tick
            if (gameMode.isStateTicking()) {
                Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    currentGameState.tick(tickCount);
                    tickCount++;
                }, 1L, 1L); // Let states figure out how they want to partition their ticks
            }
        } else {
            throw new GameModeAlreadySetException();
        }
    }

    // Only visible to states
    public void setState(@NonNull BaseGameState baseGameState) {
        if (baseGameState != currentGameState) {
            if (currentGameState != null) {
                eventManager.unregister(currentGameState);
                HandlerList.unregisterAll(currentGameState);

                currentGameState.stop(baseGameState);
            }

            baseGameState.start(currentGameState);
            currentGameState = baseGameState;

            eventManager.register(currentGameState);
            Bukkit.getPluginManager().registerEvents(currentGameState, plugin);

            log.info("EngineManager: Changing state.");
        } else {
            throw new EngineException("Game state already set to this state.");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (currentGameState != null && currentGameState instanceof EventGameState)
            ((EventGameState) currentGameState).onJoin(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (currentGameState != null && currentGameState instanceof EventGameState)
            ((EventGameState) currentGameState).onQuit(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage("");
        if (currentGameState != null && currentGameState instanceof EventGameState)
            ((EventGameState) currentGameState).onDeath(event);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (currentGameState != null && currentGameState instanceof EventGameState)
            ((EventGameState) currentGameState).onRespawn(event);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (currentGameState != null && currentGameState instanceof EventGameState) {
            EventGameState eventGameState = (EventGameState) currentGameState;
            event.setCancelled(!eventGameState.canBreak(event.getPlayer(), event.getBlock()));
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (currentGameState != null && currentGameState instanceof EventGameState) {
            EventGameState eventGameState = (EventGameState) currentGameState;
            event.setCancelled(!eventGameState.canPlace(event.getPlayer(), event.getBlock()));
        }
    }

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if (currentGameState != null && currentGameState instanceof EventGameState) {
            EventGameState eventGameState = (EventGameState) currentGameState;

            //--- Take damage
            if (event.getEntity() instanceof Player) {
                Player target = (Player) event.getEntity();
                event.setCancelled(!eventGameState.canTakeDamage(target, event.getDamager(), event.getCause()));
            }

            //--- Deal damage
            if (event.getDamager() instanceof Player) {
                Player instigator = (Player) event.getDamager();
                event.setCancelled(!eventGameState.canDealDamage(instigator, event.getEntity(), event.getCause()));
            }
        }
    }

}
