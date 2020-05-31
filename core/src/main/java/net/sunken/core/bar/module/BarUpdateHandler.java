package net.sunken.core.bar.module;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.common.database.RedisConnection;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.util.AsyncHelper;
import net.sunken.core.bar.BarHelper;
import net.sunken.core.bar.BarSettings;
import net.sunken.core.bar.packet.BarUpdatePacket;
import org.bukkit.ChatColor;
import org.bukkit.boss.BossBar;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.Optional;

@Log
public class BarUpdateHandler extends PacketHandler<BarUpdatePacket> {

    @Inject
    private BarSettings barSettings;
    @Inject
    private RedisConnection redisConnection;

    @Override
    public void onReceive(BarUpdatePacket packet) {
        AsyncHelper.executor().submit(() -> {
            try (Jedis jedis = redisConnection.getConnection()) {
                Map<String, String> kv = jedis.hgetAll(BarHelper.BAR_STORAGE_KEY + ":" + BarHelper.BAR_SETTINGS_KEY);

                BossBar bossBar = barSettings.getBossBar();
                if (kv.size() > 0) {
                    String actionMessage = kv.get(BarHelper.BAR_SETTINGS_ACTION_KEY);
                    barSettings.setAction((actionMessage == "" ? Optional.empty() : Optional.of(actionMessage)));

                    bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', kv.get(BarHelper.BAR_SETTINGS_BOSS_KEY)));
                    bossBar.setVisible(true);
                } else {
                    barSettings.setAction(Optional.empty());
                    bossBar.setVisible(false);
                }
            }

            log.info("BarUpdatePacket");
        });
    }

}
