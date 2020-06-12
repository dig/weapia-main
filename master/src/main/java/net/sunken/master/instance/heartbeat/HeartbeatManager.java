package net.sunken.master.instance.heartbeat;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.java.Log;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketHandlerRegistry;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.server.Server;
import net.sunken.common.server.module.ServerManager;
import net.sunken.common.server.packet.ServerHeartbeatPacket;
import net.sunken.common.util.AsyncHelper;
import net.sunken.master.instance.InstanceManager;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

@Log
@Singleton
public class HeartbeatManager implements Facet, Enableable {

    @Inject
    private HeartbeatRunnable heartbeatRunnable;
    @Inject
    private PacketHandlerRegistry packetHandlerRegistry;
    @Inject
    private ServerHeartbeatHandler serverHeartbeatHandler;

    @Getter
    private Map<String, Integer> heartbeatServerAttempt;

    @Override
    public void enable() {
        heartbeatServerAttempt = Maps.newConcurrentMap();
        packetHandlerRegistry.registerHandler(ServerHeartbeatPacket.class, serverHeartbeatHandler);
        AsyncHelper.scheduledExecutor().scheduleAtFixedRate(heartbeatRunnable, 1L, 1L, TimeUnit.MINUTES);
    }

    @Override
    public void disable() {
    }

    public void responded(String serverId) {
        heartbeatServerAttempt.remove(serverId);
    }

    private static class HeartbeatRunnable implements Runnable {

        @Inject
        private HeartbeatManager heartbeatManager;
        @Inject
        private ServerManager serverManager;
        @Inject
        private InstanceManager instanceManager;
        @Inject
        private PacketUtil packetUtil;

        @Override
        public void run() {
            Map<String, Integer> heartbeatServerAttempt = heartbeatManager.getHeartbeatServerAttempt();
            Queue<String> serversToClose = Queues.newLinkedBlockingQueue();

            serverManager.findAll().stream()
                    .filter(Server::canHeartbeatCheck)
                    .forEach(server -> {
                        int heartbeatCount = 0;

                        if (heartbeatServerAttempt.containsKey(server.getId())) {
                            heartbeatCount = heartbeatServerAttempt.get(server.getId());

                            if (heartbeatCount >= 2) {
                                serversToClose.add(server.getId());
                                return;
                            }
                        }

                        heartbeatServerAttempt.put(server.getId(), heartbeatCount + 1);
                    });

            packetUtil.send(new ServerHeartbeatPacket(null, ServerHeartbeatPacket.Reason.REQUEST));

            while (!serversToClose.isEmpty()) {
                String serverToCloseId = serversToClose.poll();
                Optional<Server> serverToCloseOptional = serverManager.findServerById(serverToCloseId);

                if (serverToCloseOptional.isPresent()) {
                    heartbeatServerAttempt.remove(serverToCloseId);
                    instanceManager.removeInstance(serverToCloseOptional.get(), InstanceManager.Reason.HEARTBEAT);
                    log.info(String.format("HeartbeatRunnable: Removing instance due to heartbeat check. (%s)", serverToCloseId));
                } else {
                    log.severe(String.format("HeartbeatRunnable: Tried to close non-existent server? huh (%s)", serverToCloseId));
                }
            }

            log.info("HeartbeatRunnable: Sent heartbeat request to all servers.");
        }

    }

}
