package net.sunken.bungeecord;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.sunken.bungeecord.inject.BungeeFacetLoader;
import net.sunken.common.database.RedisConnection;

public class BungeeMain extends Plugin {

    @Getter
    private Injector injector;
    private RedisConnection redisConnection;
    private BungeeFacetLoader bungeeFacetLoader;

    private boolean shutdown = false;

    @Override
    public void onEnable() {
        //--- Configure all modules
        injector = Guice.createInjector(new BungeePluginModule(this));

        //--- Connect databases
        redisConnection = injector.getInstance(RedisConnection.class);

        //--- Enable all modules
        bungeeFacetLoader = injector.getInstance(BungeeFacetLoader.class);
        bungeeFacetLoader.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.handleGraceShutdown()));
    }

    @Override
    public void onDisable() {
        //--- Disable all modules
        bungeeFacetLoader.stop();

        //--- Disconnect databases
        redisConnection.disconnect();
    }

    private void handleGraceShutdown() {
        if (!shutdown) {
            shutdown = true;

            if (getProxy().getPlayers().size() > 0) {
                //--- Broadcast
                getProxy().broadcast(TextComponent.fromLegacyText(Constants.PROXY_RESTART));

                //--- Wait 30 seconds
                try {
                    Thread.sleep(30 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //--- Shutdown
            this.onDisable();
            getProxy().stop();
        }
    }

}
