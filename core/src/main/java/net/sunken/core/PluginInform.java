package net.sunken.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import net.sunken.common.config.InjectConfig;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketHandlerRegistry;
import net.sunken.common.server.Server;
import net.sunken.common.server.ServerHelper;
import net.sunken.common.server.ServerInformer;
import net.sunken.common.server.packet.ServerHeartbeatPacket;
import net.sunken.common.util.AsyncHelper;
import net.sunken.core.config.InstanceConfiguration;
import net.sunken.core.heartbeat.ServerHeartbeatHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class PluginInform implements Facet, Enableable, Listener {

    @Inject @InjectConfig
    private InstanceConfiguration instanceConfiguration;
    @Inject
    private ServerInformer serverInformer;
    @Inject
    private JavaPlugin javaPlugin;
    @Inject
    private PacketHandlerRegistry packetHandlerRegistry;
    @Inject
    private ServerHeartbeatHandler serverHeartbeatHandler;

    @Getter
    private Server server;

    @Override
    public void enable() {
        Map<String, String> metadata = new HashMap<>();
        if (instanceConfiguration.getMetadataId().isPresent()) {
            metadata.put(ServerHelper.SERVER_METADATA_ID_KEY, instanceConfiguration.getMetadataId().get());
        }

        server = Server.builder()
                    .id(instanceConfiguration.getId())
                    .type(instanceConfiguration.getType())
                    .host(javaPlugin.getServer().getIp())
                    .port(javaPlugin.getServer().getPort())
                    .game(instanceConfiguration.getGame())
                    .world(instanceConfiguration.getWorld())
                    .players(Bukkit.getOnlinePlayers().size())
                    .maxPlayers(instanceConfiguration.getGame().getMaxPlayers())
                    .state(Server.State.PENDING)
                    .metadata(metadata)
                    .build();

        packetHandlerRegistry.registerHandler(ServerHeartbeatPacket.class, serverHeartbeatHandler);
        serverInformer.add(server, true);
    }

    @Override
    public void disable() {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        server.setPlayers(Bukkit.getOnlinePlayers().size());
        AsyncHelper.executor().submit(() -> serverInformer.update(server, true));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        server.setPlayers(Bukkit.getOnlinePlayers().size() - 1);
        AsyncHelper.executor().submit(() -> serverInformer.update(server, true));
    }

    public void setState(Server.State state) {
        server.setState(state);
        serverInformer.update(server, true);
    }

    public void remove() {
        serverInformer.remove(server.getId(), true);
    }

}
