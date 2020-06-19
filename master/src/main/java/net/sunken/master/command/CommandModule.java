package net.sunken.master.command;

import com.google.inject.*;
import net.sunken.common.command.impl.*;
import net.sunken.common.inject.*;
import net.sunken.master.command.networkcommand.*;

public class CommandModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(BaseCommandRegistry.class).toInstance(new CommandRegistry());

        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(CommandRegistry.class);

        install(new NetworkCommandModule());
    }

}
