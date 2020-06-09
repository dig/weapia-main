package net.sunken.master;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.Getter;
import net.sunken.common.database.RedisConnection;
import net.sunken.master.inject.MasterFacetLoader;

public class Master {

    @Getter
    private static Master instance;

    @Getter
    private Injector injector;
    private RedisConnection redisConnection;
    @Getter
    private MasterFacetLoader masterFacetLoader;

    public Master() {
        instance = this;
        injector = Guice.createInjector(new MasterModule());

        redisConnection = injector.getInstance(RedisConnection.class);
        masterFacetLoader = injector.getInstance(MasterFacetLoader.class);
        masterFacetLoader.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.handleGraceShutdown()));

        /*
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
         */
    }

    private void handleGraceShutdown() {
        masterFacetLoader.stop();
        redisConnection.disconnect();
    }

    public static void main(String[] args) {
        new Master();
    }

}
