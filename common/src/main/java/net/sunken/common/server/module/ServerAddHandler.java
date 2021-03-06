package net.sunken.common.server.module;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.common.database.RedisConnection;
import net.sunken.common.event.EventManager;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.server.Server;
import net.sunken.common.server.ServerHelper;
import net.sunken.common.server.module.event.ServerAddedEvent;
import net.sunken.common.server.packet.ServerAddPacket;
import net.sunken.common.util.AsyncHelper;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.logging.Level;

@Log
public class ServerAddHandler extends PacketHandler<ServerAddPacket> {

    @Inject
    private ServerManager serverManager;
    @Inject
    private RedisConnection redisConnection;
    @Inject
    private EventManager eventManager;

    @Override
    public void onReceive(ServerAddPacket packet) {
        AsyncHelper.executor().execute(() -> {
            try (Jedis jedis = redisConnection.getConnection()) {
                Map<String, String> kv = jedis.hgetAll(ServerHelper.SERVER_STORAGE_KEY + ":" + packet.getId());

                try {
                    Server server = ServerHelper.from(kv);
                    serverManager.add(server, true);
                    eventManager.callEvent(new ServerAddedEvent(server));
                } catch (Exception e) {
                    log.log(Level.SEVERE, "Unable to load server", e);
                }
            }
        });
    }
}
