package net.sunken.bungeecord.proxy.module;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.bungeecord.proxy.ProxyHelper;
import net.sunken.bungeecord.proxy.ProxySettings;
import net.sunken.bungeecord.proxy.packet.ProxyUpdatePacket;
import net.sunken.common.database.RedisConnection;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.util.AsyncHelper;
import redis.clients.jedis.Jedis;

import java.util.Map;

@Log
public class ProxyUpdateHandler extends PacketHandler<ProxyUpdatePacket> {

    @Inject
    private ProxySettings proxySettings;
    @Inject
    private RedisConnection redisConnection;

    @Override
    public void onReceive(ProxyUpdatePacket packet) {
        AsyncHelper.executor().submit(() -> {
            try (Jedis jedis = redisConnection.getConnection()) {
                Map<String, String> kv = jedis.hgetAll(ProxyHelper.PROXY_STORAGE_KEY + ":" + ProxyHelper.PROXY_SETTINGS_KEY);

                proxySettings.setMotdTopLine(kv.get(ProxyHelper.PROXY_SETTINGS_MOTDTOPLINE_KEY));
                proxySettings.setMotdBottomLine(kv.get(ProxyHelper.PROXY_SETTINGS_MOTDBOTTOMLINE_KEY));
                proxySettings.setMotdCentered(Boolean.valueOf(kv.get(ProxyHelper.PROXY_SETTINGS_MOTDCENTERED_KEY)));
            }

            log.info("ProxyUpdatePacket");
        });
    }

}
