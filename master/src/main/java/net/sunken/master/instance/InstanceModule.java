package net.sunken.master.instance;

import com.google.inject.AbstractModule;
import net.sunken.common.config.ConfigModule;
import net.sunken.common.inject.PluginFacetBinder;
import net.sunken.master.instance.config.InstanceConfiguration;
import net.sunken.master.instance.heartbeat.HeartbeatManager;

import java.io.File;

public class InstanceModule extends AbstractModule {

    @Override
    public void configure() {
        install(new ConfigModule(new File("config/instance.conf"), InstanceConfiguration.class));

        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(InstanceManager.class);
        pluginFacetBinder.addBinding(HeartbeatManager.class);
    }

}
