package net.sunken.master.instance;

import com.google.inject.AbstractModule;
import net.sunken.common.inject.PluginFacetBinder;
import net.sunken.master.instance.heartbeat.HeartbeatManager;
import net.sunken.master.instance.heartbeat.ServerHeartbeatHandler;

public class InstanceModule extends AbstractModule {

    @Override
    public void configure() {
        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(InstanceManager.class);
        pluginFacetBinder.addBinding(HeartbeatManager.class);
    }

}
