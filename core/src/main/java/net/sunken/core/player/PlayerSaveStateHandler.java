package net.sunken.core.player;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.common.database.MongoConnection;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.packet.PacketHandlerRegistry;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.PlayerManager;
import net.sunken.common.player.packet.PlayerSaveStatePacket;
import net.sunken.common.util.AsyncHelper;
import java.util.logging.Level;

@Log
public class PlayerSaveStateHandler extends PacketHandler<PlayerSaveStatePacket> implements Facet, Enableable {

    @Inject
    private PacketHandlerRegistry packetHandlerRegistry;

    @Inject
    private PlayerManager playerManager;
    @Inject
    private PacketUtil packetUtil;
    @Inject
    private MongoConnection mongoConnection;

    @Override
    public void onReceive(PlayerSaveStatePacket packet) {
        if (packet.getReason() == PlayerSaveStatePacket.Reason.REQUEST) {
            playerManager.get(packet.getUuid())
                    .map(CorePlayer.class::cast)
                    .ifPresent(corePlayer ->
                        AsyncHelper.executor().execute(() -> {
                            boolean success = true;
                            try {
                                corePlayer.save(mongoConnection, corePlayer.toPlayer().get());
                            } catch (Exception e) {
                                log.log(Level.SEVERE, "Failed to save player data.", e);
                                success = false;
                            }
                            packetUtil.send(new PlayerSaveStatePacket(corePlayer.getUuid(), success ? PlayerSaveStatePacket.Reason.COMPLETE : PlayerSaveStatePacket.Reason.FAIL));
                        })
                    );
        }
    }

    @Override
    public void enable() {
        packetHandlerRegistry.registerHandler(PlayerSaveStatePacket.class, this);
    }
}
