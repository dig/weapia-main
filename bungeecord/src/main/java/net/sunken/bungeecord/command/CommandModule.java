package net.sunken.bungeecord.command;

import com.google.inject.AbstractModule;
import net.sunken.bungeecord.command.commands.*;
import net.sunken.common.command.impl.BaseCommandRegistry;
import net.sunken.common.inject.PluginFacetBinder;

public class CommandModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(BaseCommandRegistry.class).toInstance(new CommandRegistry());

        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(BuyCommand.class);
        pluginFacetBinder.addBinding(DiscordCommand.class);
        pluginFacetBinder.addBinding(HelpCommand.class);
        pluginFacetBinder.addBinding(StaffChatCommand.class);
        pluginFacetBinder.addBinding(LobbyCommand.class);
        pluginFacetBinder.addBinding(ServerCommand.class);
        pluginFacetBinder.addBinding(CreateServerCommand.class);
        pluginFacetBinder.addBinding(GoToServerCommand.class);
    }

}
