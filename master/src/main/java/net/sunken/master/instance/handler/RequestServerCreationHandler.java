package net.sunken.master.instance.handler;

import com.google.inject.Inject;
import lombok.extern.java.Log;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.server.packet.RequestServerCreationPacket;
import net.sunken.master.instance.InstanceManager;

@Log
public class RequestServerCreationHandler extends PacketHandler<RequestServerCreationPacket> {

    @Inject
    private InstanceManager instanceManager;

    @Override
    public void onReceive(RequestServerCreationPacket packet) {
        instanceManager.create(packet.getType(), packet.getGame());
        log.info(String.format("Creating instance. (%s, %s)", packet.getType().toString(), packet.getGame().toString()));
    }

}
