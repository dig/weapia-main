package net.sunken.core.player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.extern.java.Log;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.Rank;
import net.sunken.common.player.module.PlayerManager;
import net.sunken.common.server.packet.ServerConnectedPacket;
import net.sunken.common.util.DummyObject;
import net.sunken.core.Constants;
import net.sunken.core.PluginInform;
import net.sunken.core.util.ColourUtil;
import net.sunken.core.util.TabListUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Log
@Singleton
public class ConnectHandler implements Facet, Listener {

    @Inject
    private PlayerManager playerManager;
    @Inject
    private PluginInform pluginInform;
    @Inject
    private PacketUtil packetUtil;

    @Getter
    private Cache<UUID, AbstractPlayer> pendingConnection;

    public ConnectHandler() {
        pendingConnection = CacheBuilder.newBuilder()
                .expireAfterWrite(10L, TimeUnit.SECONDS)
                .build();
    }

    //--- Event is called before any other join event.
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        AbstractPlayer abstractPlayer = pendingConnection.getIfPresent(player.getUniqueId());

        packetUtil.send(new ServerConnectedPacket(player.getUniqueId(), pluginInform.getServer().getId()));
        log.info(String.format("onPlayerJoin (%s)", player.getUniqueId().toString()));

        if (abstractPlayer != null) {
            log.info(String.format("onPlayerJoin: Successful join (%s)", player.getUniqueId().toString()));

            playerManager.add(abstractPlayer);
            pendingConnection.invalidate(player.getUniqueId());

            event.setJoinMessage("");

            abstractPlayer.setup();
        } else {
            log.severe(String.format("onPlayerJoin: Attempted to join with no data loaded? (%s)", player.getUniqueId().toString()));
            player.kickPlayer(Constants.FAILED_LOAD_DATA);
        }
    }

    public boolean handlePreLogin(AbstractPlayer abstractPlayer) {
        log.info(String.format("handlePreLogin (%s)", abstractPlayer.getUuid().toString()));

        boolean loadState = abstractPlayer.load();
        log.info(String.format("handlePreLogin: Finish load (%s)", abstractPlayer.getUuid().toString()));

        //--- TODO: Move to database, will hardcode until we have decided on a database (MongoDB, Cassandra..)
        switch (abstractPlayer.getUuid().toString()) {
            case "db073964-3bae-4efb-ba82-24a8de5ecec9":
            case "c83bc38d-11ae-4973-9d7c-5c77d6dcfb86":
                abstractPlayer.setRank(Rank.OWNER);
                break;
        }

        if (loadState) pendingConnection.put(abstractPlayer.getUuid(), abstractPlayer);
        return loadState;
    }

}