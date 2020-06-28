package net.sunken.bungeecord.party;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.sunken.bungeecord.party.handler.*;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketHandlerRegistry;
import net.sunken.common.party.packet.*;

@Singleton
public class PartyManager implements Facet, Enableable {

    @Inject
    private PacketHandlerRegistry packetHandlerRegistry;
    @Inject
    private PartyCreateResponseHandler partyCreateResponseHandler;
    @Inject
    private PartyDisbandResponseHandler partyDisbandResponseHandler;
    @Inject
    private PartyInviteResponseHandler partyInviteResponseHandler;
    @Inject
    private PartyInviteFinishResponseHandler partyInviteFinishResponseHandler;
    @Inject
    private PartyMessageHandler partyMessageHandler;
    @Inject
    private PartyLeaveResponseHandler partyLeaveResponseHandler;
    @Inject
    private PartySetLeaderResponseHandler partySetLeaderResponseHandler;
    @Inject
    private PartyKickResponseHandler partyKickResponseHandler;

    @Override
    public void enable() {
        packetHandlerRegistry.registerHandler(PartyCreateResponsePacket.class, partyCreateResponseHandler);
        packetHandlerRegistry.registerHandler(PartyDisbandResponsePacket.class, partyDisbandResponseHandler);
        packetHandlerRegistry.registerHandler(PartyInviteResponsePacket.class, partyInviteResponseHandler);
        packetHandlerRegistry.registerHandler(PartyInviteFinishResponsePacket.class, partyInviteFinishResponseHandler);
        packetHandlerRegistry.registerHandler(PartyMessagePacket.class, partyMessageHandler);
        packetHandlerRegistry.registerHandler(PartyLeaveResponsePacket.class, partyLeaveResponseHandler);
        packetHandlerRegistry.registerHandler(PartySetLeaderResponsePacket.class, partySetLeaderResponseHandler);
        packetHandlerRegistry.registerHandler(PartyKickResponsePacket.class, partyKickResponseHandler);
    }
}
