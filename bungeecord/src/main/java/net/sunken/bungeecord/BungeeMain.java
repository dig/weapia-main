package net.sunken.bungeecord;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.sunken.bungeecord.inject.BungeeFacetLoader;
import net.sunken.common.database.MongoConnection;
import net.sunken.common.database.RedisConnection;

public class BungeeMain extends Plugin {

    @Getter
    private Injector injector;
    private RedisConnection redisConnection;
    private MongoConnection mongoConnection;
    private BungeeFacetLoader bungeeFacetLoader;
    private BungeeInform bungeeInform;

    @Override
    public void onEnable() {
        injector = Guice.createInjector(new BungeePluginModule(this));

        redisConnection = injector.getInstance(RedisConnection.class);
        mongoConnection = injector.getInstance(MongoConnection.class);

        bungeeInform = injector.getInstance(BungeeInform.class);
        bungeeFacetLoader = injector.getInstance(BungeeFacetLoader.class);
        bungeeFacetLoader.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.handleGraceShutdown()));
    }

    @Override
    public void onDisable() {
        bungeeFacetLoader.stop();
    }

    private void handleGraceShutdown() {
        bungeeInform.remove();
        redisConnection.disconnect();
        mongoConnection.disconnect();
    }
}
