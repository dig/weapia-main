package net.sunken.common.player.module;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.packet.PlayerSaveStatePacket;
import net.sunken.common.util.AsyncHelper;

import java.util.Optional;

@Log
public class PlayerSaveStateHandler extends PacketHandler<PlayerSaveStatePacket> {

    @Inject
    private PlayerManager playerManager;
    @Inject
    private PacketUtil packetUtil;

    @Override
    public void onReceive(PlayerSaveStatePacket packet) {
        if (packet.getReason() == PlayerSaveStatePacket.Reason.REQUEST) {
            Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(packet.getUuid());

            if (abstractPlayerOptional.isPresent()) {
                AbstractPlayer abstractPlayer = abstractPlayerOptional.get();

                log.info(String.format("PlayerSaveStateHandler: Saving player (%s)", abstractPlayer.getUuid().toString()));
                AsyncHelper.executor().submit(() -> {
                    boolean saveState = abstractPlayer.save();

                    packetUtil.send(new PlayerSaveStatePacket(abstractPlayer.getUuid(), (saveState ? PlayerSaveStatePacket.Reason.COMPLETE : PlayerSaveStatePacket.Reason.FAIL)));
                    log.info(String.format("PlayerSaveStateHandler: Saved, sending response. (%s)", abstractPlayer.getUuid().toString()));
                });
            }
        }
    }

}
