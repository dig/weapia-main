package net.sunken.common.server.module.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.sunken.common.event.SunkenEvent;
import net.sunken.common.server.Server;

/**
 * Called when the server adds a new server to memory.
 *
 * @author Joseph Ali
 */
@Getter
@AllArgsConstructor
public class ServerAddedEvent extends SunkenEvent {

    private Server server;

}
