package net.sunken.bungeecord.proxy;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import net.sunken.bungeecord.Constants;
import net.sunken.bungeecord.proxy.module.ProxyUpdateHandler;
import net.sunken.bungeecord.proxy.packet.ProxyUpdatePacket;
import net.sunken.common.database.RedisConnection;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketHandlerRegistry;
import redis.clients.jedis.Jedis;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@Log
@Singleton
public class ProxySettings implements Facet, Enableable {

    @Inject
    private RedisConnection redisConnection;
    @Inject
    private PacketHandlerRegistry packetHandlerRegistry;
    @Inject
    private ProxyUpdateHandler proxyUpdateHandler;

    @Getter @Setter
    private String motdTopLine = Constants.DEFAULT_PING_TOP_LINE;
    @Getter @Setter
    private String motdBottomLine = Constants.DEFAULT_PING_BOTTOM_LINE;
    @Getter @Setter
    private boolean motdCentered = false;
    @Getter @Setter
    private BufferedImage favicon;

    public ProxySettings() {
        try {
            favicon = ImageIO.read(new File("config/favicon.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void enable() {
        try (Jedis jedis = redisConnection.getConnection()) {
            Map<String, String> kv = jedis.hgetAll(ProxyHelper.PROXY_STORAGE_KEY + ":" + ProxyHelper.PROXY_SETTINGS_KEY);

            if (kv.size() > 0) {
                motdTopLine = kv.get(ProxyHelper.PROXY_SETTINGS_MOTDTOPLINE_KEY);
                motdBottomLine = kv.get(ProxyHelper.PROXY_SETTINGS_MOTDBOTTOMLINE_KEY);
                motdCentered = Boolean.valueOf(kv.get(ProxyHelper.PROXY_SETTINGS_MOTDCENTERED_KEY));
            }
        }

        packetHandlerRegistry.registerHandler(ProxyUpdatePacket.class, proxyUpdateHandler);
    }

    @Override
    public void disable() {
    }

}
