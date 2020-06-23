package net.sunken.bungeecord.proxy;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.sunken.common.inject.Facet;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import net.sunken.common.server.module.ServerManager;
import net.sunken.common.util.StringUtil;

import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class PingListener implements Facet, Listener {

    @Inject
    private Plugin plugin;
    @Inject
    private ServerManager serverManager;
    @Inject
    private ProxySettings proxySettings;

    @EventHandler
    public void onPing(ProxyPingEvent event) {
        ServerPing serverPing = event.getResponse();

        Set<Server> allBungees = serverManager.findAll(Server.Type.BUNGEE, Game.NONE);
        serverPing.getPlayers().setOnline(serverManager.getTotalPlayersOnline());
        serverPing.getPlayers().setMax(allBungees.size() * 500);

        if (proxySettings.isMotdCentered()) {
            serverPing.setDescriptionComponent(new TextComponent(
                    StringUtil.center(ChatColor.translateAlternateColorCodes('&', proxySettings.getMotdTopLine()), 144) + "\n" +
                            StringUtil.center(ChatColor.translateAlternateColorCodes('&', proxySettings.getMotdBottomLine()), 124)
            ));
        } else {
            serverPing.setDescriptionComponent(new TextComponent(
                    ChatColor.translateAlternateColorCodes('&', proxySettings.getMotdTopLine()) + "\n" +
                            ChatColor.translateAlternateColorCodes('&', proxySettings.getMotdBottomLine())
            ));
        }

        serverPing.setFavicon(Favicon.create(proxySettings.getFavicon()));
        event.setResponse(serverPing);
    }

}
