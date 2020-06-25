package net.sunken.master.instance;

import com.google.inject.AbstractModule;
import net.sunken.common.config.ConfigModule;
import net.sunken.common.inject.FacetBinder;
import net.sunken.master.instance.config.InstanceConfiguration;
import net.sunken.master.instance.heartbeat.HeartbeatManager;

import java.io.File;

public class InstanceModule extends AbstractModule {

    @Override
    public void configure() {
        install(new ConfigModule(new File("config/instance.conf"), InstanceConfiguration.class));

        final FacetBinder facetBinder = new FacetBinder(binder());
        facetBinder.addBinding(InstanceManager.class);
        facetBinder.addBinding(HeartbeatManager.class);
    }
}
