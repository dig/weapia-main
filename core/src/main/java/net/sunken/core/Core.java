package net.sunken.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.Getter;
import net.sunken.common.database.RedisConnection;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.packet.PlayerRequestServerPacket;
import net.sunken.common.server.Server;
import net.sunken.core.inject.PluginFacetLoader;
import net.sunken.core.inject.PluginModule;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Core extends JavaPlugin {

    @Getter
    protected Injector injector;
    protected RedisConnection redisConnection;
    protected PluginFacetLoader pluginFacetLoader;

    protected PluginInform pluginInform;
    protected PacketUtil packetUtil;

    private boolean shutdown = false;

    public void onEnable(PluginModule pluginModule) {
        //--- Configure all modules
        injector = Guice.createInjector(pluginModule);

        //--- Connect databases
        redisConnection = injector.getInstance(RedisConnection.class);

        //--- Enable all modules
        pluginFacetLoader = injector.getInstance(PluginFacetLoader.class);
        pluginFacetLoader.start();

        pluginInform = injector.getInstance(PluginInform.class);
        packetUtil = injector.getInstance(PacketUtil.class);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.handleGraceShutdown()));
    }

    @Override
    public void onDisable() {
        //--- Disable all modules
        pluginFacetLoader.stop();

        //--- Disconnect databases
        redisConnection.disconnect();
    }

    public void handleGraceShutdown() {
        if (!shutdown) {
            shutdown = true;

            pluginInform.setState(Server.State.CLOSED);
            Bukkit.getOnlinePlayers().forEach(player -> packetUtil.send(new PlayerRequestServerPacket(player.getUniqueId(), Server.Type.LOBBY, true)));

            //--- Wait for all players to go
            int iterations = 0;
            while (Bukkit.getOnlinePlayers().size() > 0) {
                if (iterations >= (20 * 10)) {
                    break;
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                iterations++;
            }

            Bukkit.shutdown();
        }
    }

}
