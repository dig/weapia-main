package net.sunken.bungeecord.player;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.module.PlayerManager;
import net.sunken.common.player.packet.PlayerSendToServerPacket;

import java.util.Optional;

@Log
public class PlayerSendToServerHandler extends PacketHandler<PlayerSendToServerPacket> {

    @Inject
    private PlayerManager playerManager;

    @Override
    public void onReceive(PlayerSendToServerPacket packet) {
        Optional<AbstractPlayer> abstractPlayerOptional = playerManager.get(packet.getUuid());

        if (abstractPlayerOptional.isPresent()) {
            BungeePlayer bungeePlayer = (BungeePlayer) abstractPlayerOptional.get();
            bungeePlayer.connect(packet.getServerDetail());
            log.info(String.format("PlayerSendToServerHandler: Sending to server. (%s, %s)", packet.getUuid().toString(), packet.getServerDetail().getId()));
        }
    }

}
