package net.sunken.lobby.state;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.common.config.InjectConfig;
import net.sunken.common.event.ListensToEvent;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import net.sunken.common.server.module.ServerManager;
import net.sunken.common.server.module.event.ServerUpdatedEvent;
import net.sunken.core.Constants;
import net.sunken.core.engine.state.impl.BaseGameState;
import net.sunken.core.engine.state.impl.EventGameState;
import net.sunken.core.executor.BukkitSyncExecutor;
import net.sunken.core.npc.NPC;
import net.sunken.core.npc.NPCManager;
import net.sunken.core.npc.config.NPCServerConfiguration;
import net.sunken.core.util.*;
import net.sunken.lobby.config.*;
import net.sunken.lobby.player.LobbyPlayer;
import net.sunken.lobby.player.LobbyPlayerFactory;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Log
public class LobbyState extends EventGameState {

    @Inject @InjectConfig
    private LobbyConfiguration lobbyConfiguration;
    @Inject
    private net.sunken.core.player.ConnectHandler connectHandler;
    @Inject
    private LobbyPlayerFactory lobbyPlayerFactory;
    @Inject
    private NPCManager npcManager;
    @Inject
    private ServerManager serverManager;
    @Inject
    private BukkitSyncExecutor bukkitSyncExecutor;

    @Override
    public void start(BaseGameState previous) {
        lobbyConfiguration.getNpcConfigurations().forEach(npcConfiguration -> {
            NPCServerConfiguration serverConfiguration = npcConfiguration.getServerConfiguration();
            long count = serverManager.getPlayersOnline(serverConfiguration.getType(), serverConfiguration.getGame());

            List<String> displayNameFormatted = new ArrayList<>();
            for (String line : npcConfiguration.getDisplayName())
                displayNameFormatted.add(line.replaceAll("%players", String.valueOf(count)));

            NPC npc = npcManager.create(
                    npcConfiguration.getId(), displayNameFormatted, npcConfiguration.getLocationConfiguration().toLocation(),
                    npcConfiguration.getSkinConfiguration().getTexture(), npcConfiguration.getSkinConfiguration().getSignature(), true);
            npc.setInteraction(npcConfiguration.getInteractionConfiguration());
        });
    }

    @Override
    public void stop(BaseGameState next) {
        lobbyConfiguration.getNpcConfigurations().forEach(npcConfiguration -> npcManager.remove(npcConfiguration.getId()));
    }

    @Override
    public void onJoin(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(lobbyConfiguration.getSpawn().toLocation());
        player.getActivePotionEffects().clear();
    }

    @Override
    public void onQuit(Player player) {
    }

    @Override
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage("");
        event.setKeepInventory(true);

        event.getDrops().clear();
    }

    @Override
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        player.setGameMode(GameMode.ADVENTURE);
        event.setRespawnLocation(lobbyConfiguration.getSpawn().toLocation());
    }

    @Override
    public boolean canBreak(Player player, Block block) {
        return player.getGameMode() == GameMode.CREATIVE;
    }

    @Override
    public boolean canPlace(Player player, Block block) {
        return player.getGameMode() == GameMode.CREATIVE;
    }

    @Override
    public boolean canTakeDamage(Player target, Entity instigator, EntityDamageEvent.DamageCause damageCause) {
        if (instigator instanceof Player)
            return ((Player) instigator).getGameMode() == GameMode.CREATIVE;

        return false;
    }

    @Override
    public boolean canDealDamage(Player instigator, Entity target, EntityDamageEvent.DamageCause damageCause) {
        return instigator.getGameMode() == GameMode.CREATIVE;
    }

    @Override
    public void tick(int tickCount) {
        if (Ticks.to(tickCount, TimeUnit.SECONDS) % 30 == 0) { // Every 30 seconds
            Bukkit.getWorlds().forEach(world -> world.setTime(0L));
        }
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        LobbyPlayer lobbyPlayer = lobbyPlayerFactory.createPlayer(event.getUniqueId(), event.getName());

        if (connectHandler.handlePreLogin(lobbyPlayer)) {
            event.allow();
        } else {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Constants.FAILED_LOAD_DATA);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(event.toWeatherState());
    }

    @EventHandler
    public void onStructureGrow(StructureGrowEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onLeafDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (!(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)
                && !(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(player.getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setFoodLevel(15);
        event.setCancelled(true);
    }

    @ListensToEvent
    public void onServerUpdated(ServerUpdatedEvent event) {
        Server server = event.getServer();

        if (server.getGame() != Game.NONE) {
            bukkitSyncExecutor.execute(() -> {
                lobbyConfiguration.getNpcConfigurations().forEach(npcConfiguration -> {
                    NPC npc = npcManager.get(npcConfiguration.getId());

                    NPCServerConfiguration serverConfiguration = npcConfiguration.getServerConfiguration();
                    long count = serverManager.getPlayersOnline(serverConfiguration.getType(), serverConfiguration.getGame());

                    int i = 0;
                    for (String line : npcConfiguration.getDisplayName()) {
                        if (line.indexOf("%players") >= 0)
                            npc.getHologram().update(i, line.replaceAll("%players", String.valueOf(count)));

                        i++;
                    }
                });
            });
        }
    }

}