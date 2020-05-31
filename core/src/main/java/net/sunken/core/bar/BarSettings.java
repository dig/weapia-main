package net.sunken.core.bar;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import net.sunken.common.database.RedisConnection;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketHandlerRegistry;
import net.sunken.common.util.AsyncHelper;
import net.sunken.core.Constants;
import net.sunken.core.bar.module.BarUpdateHandler;
import net.sunken.core.bar.packet.BarUpdatePacket;
import net.sunken.core.util.ActionBarUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.Optional;

@Singleton
public class BarSettings implements Facet, Enableable, Listener {

    @Inject
    private RedisConnection redisConnection;
    @Inject
    private PacketHandlerRegistry packetHandlerRegistry;
    @Inject
    private BarUpdateHandler barUpdateHandler;
    @Inject
    private JavaPlugin javaPlugin;
    @Inject
    private BarRunnable barRunnable;

    @Getter @Setter
    private Optional<String> action;
    @Getter
    private BossBar bossBar;

    public BarSettings() {
        this.action = Optional.empty();
        this.bossBar = Bukkit.createBossBar(Constants.DEFAULT_BOSS_BAR, BarColor.GREEN, BarStyle.SOLID);
        this.bossBar.setVisible(false);
    }

    @Override
    public void enable() {
        try (Jedis jedis = redisConnection.getConnection()) {
            Map<String, String> kv = jedis.hgetAll(BarHelper.BAR_STORAGE_KEY + ":" + BarHelper.BAR_SETTINGS_KEY);

            if (kv.size() > 0) {
                String actionMessage = kv.get(BarHelper.BAR_SETTINGS_ACTION_KEY);
                setAction((actionMessage == "" ? Optional.empty() : Optional.of(actionMessage)));

                BossBar bossBar = getBossBar();
                bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', kv.get(BarHelper.BAR_SETTINGS_BOSS_KEY)));
                bossBar.setVisible(true);
            }
        }

        packetHandlerRegistry.registerHandler(BarUpdatePacket.class, barUpdateHandler);
        Bukkit.getScheduler().runTaskTimer(javaPlugin, barRunnable, 20L, 20L);
    }

    @Override
    public void disable() {
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        bossBar.addPlayer(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        bossBar.removePlayer(player);
    }

    private static class BarRunnable implements Runnable {

        @Inject
        private BarSettings barSettings;

        @Override
        public void run() {
            if (barSettings.getAction().isPresent()) {
                for (Player player : Bukkit.getOnlinePlayers())
                    ActionBarUtil.sendMessage(player, barSettings.getAction().get());
            }
        }

    }

}
