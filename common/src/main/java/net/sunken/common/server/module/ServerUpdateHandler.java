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
        Optional<Server> serverToUpdateOpt = serverManager.getServerList()
                .stream()
                .filter(server -> server.getId().equals(packet.getId()))
                .findFirst();

        if (serverToUpdateOpt.isPresent()) {
            Server serverToUpdate = serverToUpdateOpt.get();

            AsyncHelper.executor().submit(() -> {
                try (Jedis jedis = redisConnection.getConnection()) {
                    Map<String, String> kv = jedis.hgetAll(ServerHelper.SERVER_STORAGE_KEY + ":" + packet.getId());

                    switch (packet.getType()) {
                        case SERVER:
                            serverToUpdate.setPlayers(Integer.parseInt(kv.get(ServerHelper.SERVER_PLAYERS_KEY)));
                            serverToUpdate.setMaxPlayers(Integer.parseInt(kv.get(ServerHelper.SERVER_MAXPLAYERS_KEY)));
                            serverToUpdate.setState(Server.State.valueOf(kv.get(ServerHelper.SERVER_STATE_KEY)));

                            break;
                        case METADATA:
                            for (String key : ServerHelper.SERVER_METADATA_KEYS) {
                                if (kv.containsKey(key))
                                    serverToUpdate.getMetadata().put(key, kv.get(key));
                            }

                            break;
                    }

                    eventManager.callEvent(new ServerUpdatedEvent(serverToUpdate));
                }

                log.info(String.format("ServerUpdatePacket (%s, %s)", packet.getId(), packet.getType().toString()));
            });
        } else {
            log.severe(String.format("ServerUpdatePacket: Attempted to update non-existent server? (%s)", packet.getId()));
        }
    }

}
