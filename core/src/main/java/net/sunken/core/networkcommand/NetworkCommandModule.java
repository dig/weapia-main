package net.sunken.core.networkcommand;

import com.google.inject.*;
import net.sunken.common.command.impl.*;
import net.sunken.common.inject.*;
import net.sunken.core.command.*;
import net.sunken.core.command.commands.*;

public class NetworkCommandModule extends AbstractModule {

    @Override
    protected void configure() {
        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(NetworkCommandListener.class);
        pluginFacetBinder.addBinding(AvailableCommandsCache.class);
    }

}
