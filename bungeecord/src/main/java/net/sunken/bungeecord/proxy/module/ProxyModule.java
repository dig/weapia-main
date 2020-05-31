package net.sunken.bungeecord.proxy.module;

import com.google.inject.AbstractModule;
import net.sunken.bungeecord.proxy.PingListener;
import net.sunken.bungeecord.proxy.ProxySettings;
import net.sunken.bungeecord.proxy.command.MOTDCommand;
import net.sunken.common.inject.PluginFacetBinder;

public class ProxyModule extends AbstractModule {

    @Override
    protected void configure() {
        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(ProxySettings.class);
        pluginFacetBinder.addBinding(PingListener.class);
        pluginFacetBinder.addBinding(MOTDCommand.class);
    }

}
