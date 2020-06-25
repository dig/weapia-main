package net.sunken.bungeecord.command;

import com.google.inject.AbstractModule;
import net.sunken.bungeecord.command.commands.*;
import net.sunken.common.command.impl.BaseCommandRegistry;
import net.sunken.common.inject.FacetBinder;

public class CommandModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(BaseCommandRegistry.class).toInstance(new CommandRegistry());

        final FacetBinder facetBinder = new FacetBinder(binder());
        facetBinder.addBinding(BuyCommand.class);
        facetBinder.addBinding(DiscordCommand.class);
        // pluginFacetBinder.addBinding(HelpCommand.class);
        facetBinder.addBinding(StaffChatCommand.class);
        facetBinder.addBinding(LobbyCommand.class);
        facetBinder.addBinding(ServerCommand.class);
        facetBinder.addBinding(CreateServerCommand.class);
    }

}
