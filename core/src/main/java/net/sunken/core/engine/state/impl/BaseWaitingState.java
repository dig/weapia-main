package net.sunken.core.engine.state.impl;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.extern.java.Log;
import net.sunken.common.config.InjectConfig;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.module.PlayerManager;
import net.sunken.common.server.Game;
import net.sunken.core.Constants;
import net.sunken.core.PluginInform;
import net.sunken.core.engine.state.config.WaitingConfiguration;
import net.sunken.core.player.CorePlayer;
import net.sunken.core.scoreboard.ScoreboardManager;
import net.sunken.core.scoreboard.ScoreboardWrapper;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.potion.PotionEffect;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Log
@Getter
public abstract class BaseWaitingState extends EventGameState {

    @Inject @InjectConfig
    protected WaitingConfiguration waitingConfiguration;
    @Inject
    private ScoreboardManager scoreboardManager;

    protected BaseGameState nextState;
    protected long startTimeMillis;

    @Override
    public void start(BaseGameState previous) {
        World world = Bukkit.getWorld(waitingConfiguration.getLocationConfiguration().getWorld());
        world.getEntities().stream()
                .filter(entity -> entity.getType() != EntityType.ITEM_FRAME && entity.getType() != EntityType.PAINTING)
                .forEach(entity -> entity.remove());
        world.setTime(0L);

        startTimeMillis = System.currentTimeMillis() + waitingConfiguration.getCountdown();
    }

    @Override
    public void stop(BaseGameState next) {
        playerManager.getOnlinePlayers().forEach(abstractPlayer -> {
            CorePlayer corePlayer = (CorePlayer) abstractPlayer;

            if (corePlayer.getScoreboardWrapper() != null)
                corePlayer.getScoreboardWrapper().removeAllEntries();
        });

        if (pluginInform.getServer().getState() == net.sunken.common.server.Server.State.OPEN)
            pluginInform.setState(net.sunken.common.server.Server.State.CLOSED);
    }

    @Override
    public void onJoin(Player player) {
        Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(player.getUniqueId());

        //--- Base
        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(waitingConfiguration.getLocationConfiguration().toLocation());

        //--- Remove potion effects
        for (PotionEffect potionEffect : player.getActivePotionEffects())
            player.removePotionEffect(potionEffect.getType());

        //--- Scoreboard
        if (abstractPlayerOptional.isPresent()) {
            CorePlayer corePlayer = (CorePlayer) abstractPlayerOptional.get();

            Game game = pluginInform.getServer().getGame();
            net.sunken.common.server.World world = pluginInform.getServer().getWorld();

            ScoreboardWrapper scoreboardWrapper = new ScoreboardWrapper(ChatColor.AQUA + "" + ChatColor.BOLD + game.getFriendlyName(), scoreboardManager);
            scoreboardWrapper.add("Spacer1", ChatColor.WHITE + " ", 10);

            scoreboardWrapper.add("MapTitle", ChatColor.WHITE + "Map", 9);
            scoreboardWrapper.add("MapValue", ChatColor.GOLD + world.getFriendlyName(), 8);
            scoreboardWrapper.add("Spacer2", ChatColor.WHITE + " ", 7);

            ChatColor playersColour = ChatColor.GREEN;
            if (playerManager.getOnlinePlayers().size() >= waitingConfiguration.getRequiredPlayers()) {
                playersColour = ChatColor.YELLOW;
            } else if (playerManager.getOnlinePlayers().size() >= pluginInform.getServer().getMaxPlayers()) {
                playersColour = ChatColor.RED;
            }

            scoreboardWrapper.add("PlayersTitle", ChatColor.WHITE + "Players", 6);
            scoreboardWrapper.add("PlayersValue", playersColour + "" + playerManager.getOnlinePlayers().size() + "/" + game.getMaxPlayers(), 5);
            scoreboardWrapper.add("Spacer3", ChatColor.WHITE + " ", 4);

            long timeDiff = startTimeMillis - System.currentTimeMillis();
            scoreboardWrapper.add("StartingTitle", ChatColor.WHITE + "Starting in", 3);
            scoreboardWrapper.add("StartingValue", ChatColor.LIGHT_PURPLE + String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(timeDiff), TimeUnit.MILLISECONDS.toSeconds(timeDiff) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDiff))), 2);
            scoreboardWrapper.add("Spacer4", ChatColor.WHITE + " ", 1);

            scoreboardWrapper.add("URL", ChatColor.GRAY + "play.weapia.com", 0);

            scoreboardWrapper.add(player);
            corePlayer.setScoreboardWrapper(scoreboardWrapper);
        } else {
            log.severe(String.format("Player not present in local playerManager, huh? (%s)", player.getName()));
        }
    }

    @Override
    public void onQuit(Player player) {}

    @Override
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage("");
        event.getDrops().clear();
    }

    @Override
    public void onRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(waitingConfiguration.getLocationConfiguration().toLocation());
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
    public boolean canTakeDamage(Player target, Entity instigator, EntityDamageEvent.DamageCause damageCause) {
        return false;
    }

    @Override
    public boolean canDealDamage(Player instigator, Entity target, EntityDamageEvent.DamageCause damageCause) {
        return false;
    }

    @Override
    public void tick(int tickCount) {
        //--- Teleport players back to spawn
        if (tickCount % (10 * 20) == 0) {
            Location spawnPoint = waitingConfiguration.getLocationConfiguration().toLocation();
            long spawnTeleportBackRadiusSquared = (waitingConfiguration.getSpawnTeleportBackRadius() * waitingConfiguration.getSpawnTeleportBackRadius());

            Bukkit.getOnlinePlayers().forEach(player -> {
                double distanceToSpawn = player.getLocation().distanceSquared(spawnPoint);

                if (distanceToSpawn >= spawnTeleportBackRadiusSquared) {
                    player.teleport(spawnPoint);
                }
            });
        }

        //--- Always day
        if (tickCount % (30 * 20) == 0) {
            Bukkit.getWorlds().forEach(world -> world.setTime(0L));
        }

        //--- Check if game should start
        if (tickCount % 10 == 0) {
            long timeDiff = startTimeMillis - System.currentTimeMillis();

            if (timeDiff > 0) {
                waitingConfiguration.getShortenTimer().forEach(timerConfiguration -> {
                    if (playerManager.getOnlinePlayers().size() >= timerConfiguration.getRequired() && timeDiff > timerConfiguration.getTimeLeft()) {
                        startTimeMillis = System.currentTimeMillis() + timerConfiguration.getTimeLeft();
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', timerConfiguration.getMessage()));
                    }
                });
            } else {
                if (playerManager.getOnlinePlayers().size() >= waitingConfiguration.getRequiredPlayers()) {
                    if (nextState != null) {
                        engineManager.setState(nextState);
                    } else {
                        startTimeMillis = System.currentTimeMillis() + waitingConfiguration.getCountdown();
                        Bukkit.broadcastMessage(Constants.COUNTDOWN_FAILED_STATE_NULL);
                    }
                } else {
                    startTimeMillis = System.currentTimeMillis() + waitingConfiguration.getCountdown();
                    Bukkit.broadcastMessage(Constants.COUNTDOWN_FAILED_REQUIRED);

                    //--- Reopen server
                    if (pluginInform.getServer().getState() != net.sunken.common.server.Server.State.OPEN)
                        pluginInform.setState(net.sunken.common.server.Server.State.OPEN);
                }
            }
        }

        //--- Countdown
        if (tickCount % 20 == 0) {
            long timeDiff = startTimeMillis - System.currentTimeMillis();

            if (timeDiff >= 10000 && timeDiff <= 11000) {
                Bukkit.broadcastMessage(String.format(Constants.COUNTDOWN_LOBBY_SECONDS, 10));
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 2F));
            } else if (timeDiff <= 5000 && playerManager.getOnlinePlayers().size() >= waitingConfiguration.getRequiredPlayers() && pluginInform.getServer().getState() == net.sunken.common.server.Server.State.OPEN) {
                pluginInform.setState(net.sunken.common.server.Server.State.CLOSED);
                log.info("State set to CLOSED.");
            } else if (timeDiff >= 3000 && timeDiff <= 4000) {
                Bukkit.broadcastMessage(String.format(Constants.COUNTDOWN_LOBBY_SECONDS, 3));
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 2F));
            } else if (timeDiff >= 2000 && timeDiff < 3000) {
                Bukkit.broadcastMessage(String.format(Constants.COUNTDOWN_LOBBY_SECONDS, 2));
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 2F));
            } else if (timeDiff >= 1000 && timeDiff < 2000) {
                Bukkit.broadcastMessage(String.format(Constants.COUNTDOWN_LOBBY_SECONDS, 1));
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 2F));
            } else if (timeDiff >= 0 && timeDiff < 1000 && playerManager.getOnlinePlayers().size() >= waitingConfiguration.getRequiredPlayers()) {
                Bukkit.broadcastMessage(Constants.COUNTDOWN_STARTING);
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 0F));
            }
        }

        //--- Scoreboard
        if (tickCount % 20 == 0) {
            long timeDiff = startTimeMillis - System.currentTimeMillis();
            String timeFormat = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(timeDiff), TimeUnit.MILLISECONDS.toSeconds(timeDiff) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDiff)));

            playerManager.getOnlinePlayers().forEach(abstractPlayer -> {
                CorePlayer corePlayer = (CorePlayer) abstractPlayer;

                Game game = pluginInform.getServer().getGame();
                ScoreboardWrapper scoreboardWrapper = corePlayer.getScoreboardWrapper();

                if (scoreboardWrapper != null) {
                    ChatColor playersColour = ChatColor.GREEN;
                    if (playerManager.getOnlinePlayers().size() >= waitingConfiguration.getRequiredPlayers()) {
                        playersColour = ChatColor.YELLOW;
                    } else if (playerManager.getOnlinePlayers().size() >= pluginInform.getServer().getMaxPlayers()) {
                        playersColour = ChatColor.RED;
                    }

                    if (scoreboardWrapper.getEntry("PlayersValue") != null) {
                        scoreboardWrapper.getEntry("PlayersValue").update(playersColour + " " + playerManager.getOnlinePlayers().size() + "/" + game.getMaxPlayers());
                        scoreboardWrapper.getEntry("StartingValue").update(((timeDiff <= (10 * 1000)) && ((timeDiff / 1000) % 2000 == 0) ? ChatColor.WHITE : ChatColor.LIGHT_PURPLE) + " " + timeFormat);
                    }
                }
            });
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        event.getDrops().clear();
        event.setDroppedExp(0);
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
        event.setCancelled(event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.EGG);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(player.getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setFoodLevel(15);
        event.setCancelled(true);
    }

}
