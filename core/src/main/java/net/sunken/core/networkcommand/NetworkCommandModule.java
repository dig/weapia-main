package net.sunken.core.command;

import com.google.inject.AbstractModule;
import net.sunken.common.command.impl.BaseCommandRegistry;
import net.sunken.common.inject.PluginFacetBinder;
import net.sunken.core.command.commands.WhereCommand;

public class CommandModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(BaseCommandRegistry.class).toInstance(new CommandRegistry());

        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(CommandRegistry.class);
        pluginFacetBinder.addBinding(WhereCommand.class);
    }

}
