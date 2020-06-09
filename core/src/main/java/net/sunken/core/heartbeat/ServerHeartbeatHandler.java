package net.sunken.core.heartbeat;

import com.google.inject.Inject;
import net.sunken.common.packet.PacketHandler;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.server.packet.ServerHeartbeatPacket;
import net.sunken.core.PluginInform;
import net.sunken.core.executor.BukkitSyncExecutor;
import redis.clients.jedis.Jedis;

public class ServerHeartbeatHandler extends PacketHandler<ServerHeartbeatPacket> {

    @Inject
    private PacketUtil packetUtil;
    @Inject
    private PluginInform pluginInform;
    @Inject
    private BukkitSyncExecutor bukkitSyncExecutor;

    @Override
    public void onReceive(ServerHeartbeatPacket packet) {
        if (packet.getReason() == ServerHeartbeatPacket.Reason.REQUEST) {
            bukkitSyncExecutor.execute(new ServerRespondRunnable());
        }
    }

    private class ServerRespondRunnable implements Runnable {

        @Override
        public void run() {
            packetUtil.sendSync(new ServerHeartbeatPacket(pluginInform.getServer().getId(), ServerHeartbeatPacket.Reason.RESPOND));
        }

    }

}
