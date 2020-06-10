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

    private BungeeInform bungeeInform;

    private boolean shutdown = false;

    @Override
    public void onEnable() {
        injector = Guice.createInjector(new BungeePluginModule(this));

        redisConnection = injector.getInstance(RedisConnection.class);
        bungeeFacetLoader = injector.getInstance(BungeeFacetLoader.class);
        bungeeFacetLoader.start();

        bungeeInform = injector.getInstance(BungeeInform.class);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.handleGraceShutdown()));
    }

    @Override
    public void onDisable() {
        bungeeFacetLoader.stop();
    }

    private void handleGraceShutdown() {
        if (!shutdown) {
            shutdown = true;

            if (getProxy().getPlayers().size() > 0) {
                getProxy().broadcast(TextComponent.fromLegacyText(Constants.PROXY_RESTART));

                try {
                    Thread.sleep(30 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            bungeeInform.remove();
            redisConnection.disconnect();
        }
    }

}
