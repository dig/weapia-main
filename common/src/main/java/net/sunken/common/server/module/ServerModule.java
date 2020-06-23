package net.sunken.common.server.module;

import com.google.inject.AbstractModule;
import net.sunken.common.inject.PluginFacetBinder;

public class ServerModule extends AbstractModule {

    @Override
    public void configure() {
        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(ServerManager.class);
    }
}
