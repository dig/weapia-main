package net.sunken.common;

import com.google.inject.AbstractModule;
import net.sunken.common.config.ConfigModule;
import net.sunken.common.database.config.RedisConfiguration;
import net.sunken.common.packet.PacketModule;

import java.io.File;

public class CommonModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ConfigModule(new File("config/redis.conf"), RedisConfiguration.class));
        install(new PacketModule());
    }

}
