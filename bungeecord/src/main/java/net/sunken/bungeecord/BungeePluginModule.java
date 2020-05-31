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
import net.sunken.bungeecord.proxy.module.ProxyModule;
import net.sunken.common.CommonModule;
import net.sunken.common.config.ConfigModule;
import net.sunken.common.inject.PluginFacetBinder;
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
        install(new ProxyModule());
        install(new CommandModule());

        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(BungeeInform.class);
        pluginFacetBinder.addBinding(ConnectHandler.class);
        pluginFacetBinder.addBinding(DisconnectHandler.class);
        pluginFacetBinder.addBinding(ChatHandler.class);
        pluginFacetBinder.addBinding(StaffMessageHandler.class);
        pluginFacetBinder.addBinding(PartyManager.class);
        pluginFacetBinder.addBinding(PartyCommand.class);
        pluginFacetBinder.addBinding(PartyChatCommand.class);
    }

}
