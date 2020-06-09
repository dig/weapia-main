package net.sunken.common.networkcommand;

import lombok.*;
import net.sunken.common.packet.*;
import net.sunken.common.player.*;

@Getter
@AllArgsConstructor
public class NetworkCommandPacket extends Packet {

    private final String command;
    private final PlayerDetail sender;
}
