package net.sunken.common.network;

import com.google.inject.AbstractModule;
import net.sunken.common.inject.PluginFacetBinder;

public class NetworkModule extends AbstractModule {

    @Override
    public void configure() {
        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(NetworkManager.class);
    }

}
