package net.sunken.common.server.module.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.event.SunkenEvent;
import net.sunken.common.server.Server;

/**
 * Called when the server updates a server in memory.
 *
 * @author Joseph Ali
 */
@Getter
@AllArgsConstructor
public class ServerUpdatedEvent extends SunkenEvent {

    private Server server;

}
