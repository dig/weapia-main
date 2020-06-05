package net.sunken.core.engine.state.impl;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.extern.java.Log;
import net.sunken.common.config.InjectConfig;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.server.Game;
import net.sunken.core.Constants;
import net.sunken.core.engine.state.config.WaitingConfiguration;
import net.sunken.core.player.CorePlayer;
import net.sunken.core.scoreboard.CustomScoreboard;
import net.sunken.core.scoreboard.ScoreboardRegistry;
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

    protected static final String SCOREBOARD_KEY = "Waiting";

    @Inject @InjectConfig
    protected WaitingConfiguration waitingConfiguration;
    @Inject
    protected ScoreboardRegistry scoreboardRegistry;

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
        setupScoreboard();
    }

    private void setupScoreboard() {
        Game game = pluginInform.getServer().getGame();
        CustomScoreboard customScoreboard = new CustomScoreboard(ChatColor.AQUA + "" + ChatColor.BOLD + "WEAPIA");
        customScoreboard.createEntry("Spacer1", ChatColor.RED + " ", 11);
        customScoreboard.createEntry("MapTitle", ChatColor.WHITE + "Map", 10);
        customScoreboard.createEntry("MapValue", ChatColor.GOLD + "" + pluginInform.getServer().getWorld().getFriendlyName(), 9);
        customScoreboard.createEntry("Spacer2", ChatColor.BLACK + " ", 8);

        ChatColor playersColour = ChatColor.GREEN;
        if (playerManager.getOnlinePlayers().size() >= waitingConfiguration.getRequiredPlayers()) {
            playersColour = ChatColor.YELLOW;
        } else if (playerManager.getOnlinePlayers().size() >= pluginInform.getServer().getMaxPlayers()) {
            playersColour = ChatColor.RED;
        }

        customScoreboard.createEntry("PlayersTitle", ChatColor.WHITE + "Players", 7);
        customScoreboard.createEntry("PlayersValue", playersColour + "" + playerManager.getOnlinePlayers().size() + "/" + game.getMaxPlayers(), 6);
        customScoreboard.createEntry("Spacer3", ChatColor.WHITE + " ", 5);

        long timeDiff = startTimeMillis - System.currentTimeMillis();
        customScoreboard.createEntry("StartingTitle", ChatColor.WHITE + "Starting in", 4);
        customScoreboard.createEntry("StartingValue", ChatColor.LIGHT_PURPLE + "" + String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(timeDiff), TimeUnit.MILLISECONDS.toSeconds(timeDiff) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDiff))), 3);
        customScoreboard.createEntry("Spacer4", ChatColor.GRAY + " ", 2);

        customScoreboard.createEntry("ServerID", ChatColor.GRAY + pluginInform.getServer().getId(), 1);
        customScoreboard.createEntry("URL", ChatColor.LIGHT_PURPLE + "play.weapia.com", 0);

        Bukkit.getOnlinePlayers().forEach(customScoreboard::add);
        scoreboardRegistry.register(SCOREBOARD_KEY, customScoreboard);
    }

    @Override
    public void stop(BaseGameState next) {
        scoreboardRegistry.unregister(SCOREBOARD_KEY);
        if (pluginInform.getServer().getState() == net.sunken.common.server.Server.State.OPEN)
            pluginInform.setState(net.sunken.common.server.Server.State.CLOSED);
    }

    @Override
    public void onJoin(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(waitingConfiguration.getLocationConfiguration().toLocation());

        Optional<CustomScoreboard> scoreboardOptional = scoreboardRegistry.get(SCOREBOARD_KEY);
        if (scoreboardOptional.isPresent()) {
            CustomScoreboard scoreboard = scoreboardOptional.get();
            scoreboard.add(player);
        }
    }

    @Override
    public void onQuit(Player player) {
        scoreboardRegistry.unregister(player.getUniqueId().toString());
    }

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

            Game game = pluginInform.getServer().getGame();
            Optional<CustomScoreboard> customScoreboardOptional = scoreboardRegistry.get(SCOREBOARD_KEY);

            if (customScoreboardOptional.isPresent()) {
                CustomScoreboard customScoreboard = customScoreboardOptional.get();

                ChatColor playersColour = ChatColor.GREEN;
                if (playerManager.getOnlinePlayers().size() >= waitingConfiguration.getRequiredPlayers()) {
                    playersColour = ChatColor.YELLOW;
                } else if (playerManager.getOnlinePlayers().size() >= pluginInform.getServer().getMaxPlayers()) {
                    playersColour = ChatColor.RED;
                }

                if (customScoreboard.getEntry("PlayersValue") != null) {
                    customScoreboard.getEntry("PlayersValue").update(playersColour + "" + playerManager.getOnlinePlayers().size() + "/" + game.getMaxPlayers());
                    customScoreboard.getEntry("StartingValue").update(((timeDiff <= (10 * 1000)) && ((timeDiff / 1000) % 2000 == 0) ? ChatColor.WHITE : ChatColor.LIGHT_PURPLE) + " " + timeFormat);
                }
            }
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
