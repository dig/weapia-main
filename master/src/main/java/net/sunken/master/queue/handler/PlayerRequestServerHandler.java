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
import net.sunken.common.util.AsyncHelper;
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

    private final Cache<UUID, InstanceDetail> pendingSaveBeforeConnect;

    public PlayerRequestServerHandler() {
        this.pendingSaveBeforeConnect = CacheBuilder.newBuilder()
                .expireAfterWrite(30L, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public void onReceive(PlayerRequestServerPacket packet) {
        log.info("PlayerRequestServerPacket");
        if (packet.isSave()) {
            if (pendingSaveBeforeConnect.getIfPresent(packet.getUuid()) == null
                    && !serverManager.hasPendingConnection(packet.getUuid())) {

                pendingSaveBeforeConnect.put(packet.getUuid(), new InstanceDetail(packet.getType(), packet.getGame()));
                AsyncHelper.executor().execute(() -> packetUtil.send(new PlayerSaveStatePacket(packet.getUuid(), PlayerSaveStatePacket.Reason.REQUEST)));
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
                    pendingSaveBeforeConnect.invalidate(uuid);
                    queueManager.queue(uuid, instanceDetail.getType(), instanceDetail.getGame());
                    break;
                case FAIL:
                    pendingSaveBeforeConnect.invalidate(uuid);
                    break;
            }
        }
    }
}