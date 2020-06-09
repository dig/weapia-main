package net.sunken.master.queue.handler;

import com.google.inject.Inject;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.packet.PlayerRequestServerIDPacket;
import net.sunken.common.player.packet.PlayerSendToServerPacket;
import net.sunken.common.server.Server;
import net.sunken.common.server.module.ServerManager;

import java.util.Optional;

public class PlayerRequestServerIDHandler extends PacketHandler<PlayerRequestServerIDPacket> {

    @Inject
    private ServerManager serverManager;
    @Inject
    private PacketUtil packetUtil;

    @Override
    public void onReceive(PlayerRequestServerIDPacket packet) {
        Optional<Server> serverOptional = serverManager.findServerById(packet.getServerID());
        if (serverOptional.isPresent()) {
            Server server = serverOptional.get();
            packetUtil.send(new PlayerSendToServerPacket(packet.getUuid(), server.toServerDetail()));
        }
    }

}
