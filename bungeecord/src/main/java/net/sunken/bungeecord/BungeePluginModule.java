package net.sunken.bungeecord;

import com.google.inject.AbstractModule;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.sunken.bungeecord.chat.StaffMessageHandler;
import net.sunken.bungeecord.chat.config.ChatConfiguration;
import net.sunken.bungeecord.chat.ChatHandler;
import net.sunken.bungeecord.command.CommandModule;
import net.sunken.bungeecord.party.PartyChatCommand;
import net.sunken.bungeecord.party.PartyCommand;
import net.sunken.bungeecord.party.PartyManager;
import net.sunken.bungeecord.player.ConnectHandler;
import net.sunken.bungeecord.player.DisconnectHandler;
import net.sunken.common.CommonModule;
import net.sunken.common.config.ConfigModule;
import net.sunken.common.inject.FacetBinder;
import net.sunken.common.server.module.ServerModule;

import java.io.File;

public class BungeePluginModule extends AbstractModule {

    protected final Plugin plugin;

    public BungeePluginModule(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(Plugin.class).toInstance(plugin);
        bind(ProxyServer.class).toInstance(plugin.getProxy());

        install(new ConfigModule(new File("config/chat.conf"), ChatConfiguration.class));

        install(new CommonModule());
        install(new ServerModule());
        install(new CommandModule());

        final FacetBinder facetBinder = new FacetBinder(binder());
        facetBinder.addBinding(BungeeInform.class);
        facetBinder.addBinding(ConnectHandler.class);
        facetBinder.addBinding(DisconnectHandler.class);
        facetBinder.addBinding(ChatHandler.class);
        facetBinder.addBinding(StaffMessageHandler.class);
        facetBinder.addBinding(PartyManager.class);
        facetBinder.addBinding(PartyCommand.class);
        facetBinder.addBinding(PartyChatCommand.class);
    }
}
