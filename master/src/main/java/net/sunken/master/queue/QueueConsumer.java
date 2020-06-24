package net.sunken.master.queue;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import net.sunken.common.server.Game;
import net.sunken.master.queue.impl.AbstractBalancer;
import net.sunken.master.queue.impl.LobbyBalancer;

import java.util.Map;

@Log
@AllArgsConstructor
public class QueueConsumer extends Thread {

    private final Map<Game, AbstractBalancer> balancers;
    private final AbstractBalancer lobbyBalancer;

    @Override
    public void run() {
        while (true) {
            lobbyBalancer.run();
            balancers.values().forEach(AbstractBalancer::run);
        }
    }
}
