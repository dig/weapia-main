package net.sunken.master.command.networkcommand;

import com.google.inject.*;
import net.sunken.common.inject.*;

public class NetworkCommandModule extends AbstractModule {

    @Override
    public void configure() {
        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(AvailableCommandsRecorder.class);
        pluginFacetBinder.addBinding(NetworkCommandHandler.class);
    }

}
