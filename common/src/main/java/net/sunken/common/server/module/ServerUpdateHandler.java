package net.sunken.common.server.module;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.common.database.RedisConnection;
import net.sunken.common.event.EventManager;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.server.Server;
import net.sunken.common.server.ServerHelper;
import net.sunken.common.server.module.event.ServerUpdatedEvent;
import net.sunken.common.server.packet.ServerUpdatePacket;
import net.sunken.common.util.AsyncHelper;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class ServerUpdateHandler extends PacketHandler<ServerUpdatePacket> {

    @Inject
    private ServerManager serverManager;
    @Inject
    private RedisConnection redisConnection;
    @Inject
    private EventManager eventManager;

    @Override
    public void onReceive(ServerUpdatePacket packet) {
        AsyncHelper.executor().execute(() ->
            serverManager.findServerById(packet.getId())
                    .ifPresent(server -> {
                        try (Jedis jedis = redisConnection.getConnection()) {
                            Map<String, String> kv = jedis.hgetAll(ServerHelper.SERVER_STORAGE_KEY + ":" + packet.getId());

                            try {
                                switch (packet.getType()) {
                                    case SERVER:
                                        server.setPlayers(Integer.parseInt(kv.get(ServerHelper.SERVER_PLAYERS_KEY)));
                                        server.setMaxPlayers(Integer.parseInt(kv.get(ServerHelper.SERVER_MAXPLAYERS_KEY)));
                                        server.setState(Server.State.valueOf(kv.get(ServerHelper.SERVER_STATE_KEY)));
                                        break;
                                    case METADATA:
                                        for (String key : ServerHelper.SERVER_METADATA_KEYS) {
                                            if (kv.containsKey(key)) {
                                                server.getMetadata().put(key, kv.get(key));
                                            }
                                        }
                                        break;
                                }

                                eventManager.callEvent(new ServerUpdatedEvent(server));
                            } catch (Exception e) {
                                log.log(Level.SEVERE, String.format("Unable to update server %s %s", packet.getId(), kv.keySet().toString()), e);
                            }
                        }
                    })
        );
    }
}
