package net.sunken.core.command;

import com.google.inject.AbstractModule;
import net.sunken.common.command.impl.BaseCommandRegistry;
import net.sunken.common.inject.FacetBinder;
import net.sunken.core.command.commands.WhereCommand;

public class CommandModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(BaseCommandRegistry.class).toInstance(new CommandRegistry());

        final FacetBinder facetBinder = new FacetBinder(binder());
        facetBinder.addBinding(CommandRegistry.class);
        facetBinder.addBinding(WhereCommand.class);
    }

}
