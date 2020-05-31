package net.sunken.master.queue.handler;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.java.Log;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.packet.PlayerRequestServerPacket;
import net.sunken.common.player.packet.PlayerSaveStatePacket;
import net.sunken.common.server.module.ServerManager;
import net.sunken.master.instance.InstanceDetail;
import net.sunken.master.queue.QueueManager;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Log
@Singleton
public class PlayerRequestServerHandler extends PacketHandler<PlayerRequestServerPacket> {

    @Inject
    private QueueManager queueManager;
    @Inject
    private ServerManager serverManager;
    @Inject
    private PacketUtil packetUtil;

    private Cache<UUID, InstanceDetail> pendingSaveBeforeConnect;

    public PlayerRequestServerHandler() {
        pendingSaveBeforeConnect = CacheBuilder.newBuilder()
                .expireAfterWrite(10L, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public void onReceive(PlayerRequestServerPacket packet) {
        if (packet.isSave()) {
            if (pendingSaveBeforeConnect.getIfPresent(packet.getUuid()) == null
                    && !serverManager.hasPendingConnection(packet.getUuid())) {

                pendingSaveBeforeConnect.put(packet.getUuid(), new InstanceDetail(packet.getType(), packet.getGame()));
                packetUtil.send(new PlayerSaveStatePacket(packet.getUuid(), PlayerSaveStatePacket.Reason.REQUEST));
            } else {
                log.info(String.format("PlayerRequestServerHandler: Tried to save player again? skipping. (%s)", packet.getUuid().toString()));
            }
        } else {
            queueManager.queue(packet.getUuid(), packet.getType(), packet.getGame());
        }
    }

    public void connected(UUID uuid, PlayerSaveStatePacket.Reason reason) {
        InstanceDetail instanceDetail = pendingSaveBeforeConnect.getIfPresent(uuid);

        if (instanceDetail != null) {
            switch (reason) {
                case COMPLETE:
                    log.info(String.format("PlayerSaveStatePacket: Complete (%s)", uuid.toString()));

                    pendingSaveBeforeConnect.invalidate(uuid);
                    queueManager.queue(uuid, instanceDetail);
                    break;
                case FAIL:
                    log.severe(String.format("PlayerSaveStatePacket: Fail (%s)", uuid.toString()));
                    pendingSaveBeforeConnect.invalidate(uuid);
                    break;
            }
        } else {
            log.severe(String.format("PlayerSaveStatePacket: Connected but doesn't exist in pendingSave? huh (%s)", uuid.toString()));
        }
    }

}
