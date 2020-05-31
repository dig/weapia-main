package net.sunken.bungeecord.proxy.command;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.sunken.bungeecord.Constants;
import net.sunken.bungeecord.command.BungeeCommand;
import net.sunken.bungeecord.proxy.ProxyHelper;
import net.sunken.bungeecord.proxy.ProxySettings;
import net.sunken.bungeecord.proxy.packet.ProxyUpdatePacket;
import net.sunken.common.command.Command;
import net.sunken.common.command.SubCommand;
import net.sunken.common.database.RedisConnection;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.Rank;
import net.sunken.common.util.AsyncHelper;
import redis.clients.jedis.Jedis;

import java.util.Optional;

@Command(aliases = {"motd"}, rank = Rank.OWNER, usage = "/motd <top|bottom|center> <value>", min = 1)
public class MOTDCommand extends BungeeCommand {

    @Inject
    private RedisConnection redisConnection;
    @Inject
    private PacketUtil packetUtil;
    @Inject
    private ProxySettings proxySettings;

    @Override
    public boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: /motd <top|bottom|center> <value>"));
        return true;
    }

    @SubCommand(aliases = {"top", "topline"}, rank = Rank.OWNER, min = 1)
    public boolean top(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        String value = String.join(" ", args);

        AsyncHelper.executor().submit(() -> {
            try (Jedis jedis = redisConnection.getConnection()) {
                ImmutableMap.Builder<String, String> proxySettingsBuilder = ImmutableMap.<String, String>builder()
                        .put(ProxyHelper.PROXY_SETTINGS_MOTDTOPLINE_KEY, value)
                        .put(ProxyHelper.PROXY_SETTINGS_MOTDBOTTOMLINE_KEY, proxySettings.getMotdBottomLine())
                        .put(ProxyHelper.PROXY_SETTINGS_MOTDCENTERED_KEY, proxySettings.isMotdCentered() + "");

                jedis.hmset(ProxyHelper.PROXY_STORAGE_KEY + ":" + ProxyHelper.PROXY_SETTINGS_KEY, proxySettingsBuilder.build());
            }

            packetUtil.send(new ProxyUpdatePacket());
        });

        commandSender.sendMessage(TextComponent.fromLegacyText(Constants.MOTD_SUCCESS_CHANGE));
        return true;
    }

    @SubCommand(aliases = {"bottom", "bottomline"}, rank = Rank.OWNER, min = 1)
    public boolean bottom(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        String value = String.join(" ", args);

        AsyncHelper.executor().submit(() -> {
            try (Jedis jedis = redisConnection.getConnection()) {
                ImmutableMap.Builder<String, String> proxySettingsBuilder = ImmutableMap.<String, String>builder()
                        .put(ProxyHelper.PROXY_SETTINGS_MOTDTOPLINE_KEY, proxySettings.getMotdTopLine())
                        .put(ProxyHelper.PROXY_SETTINGS_MOTDBOTTOMLINE_KEY, value)
                        .put(ProxyHelper.PROXY_SETTINGS_MOTDCENTERED_KEY, proxySettings.isMotdCentered() + "");

                jedis.hmset(ProxyHelper.PROXY_STORAGE_KEY + ":" + ProxyHelper.PROXY_SETTINGS_KEY, proxySettingsBuilder.build());
            }

            packetUtil.send(new ProxyUpdatePacket());
        });

        commandSender.sendMessage(TextComponent.fromLegacyText(Constants.MOTD_SUCCESS_CHANGE));
        return true;
    }

    @SubCommand(aliases = {"center"}, rank = Rank.OWNER, min = 1, max = 1)
    public boolean center(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        String value = args[0];

        if (value.equals("true") || value.equals("false")) {
            AsyncHelper.executor().submit(() -> {
                try (Jedis jedis = redisConnection.getConnection()) {
                    ImmutableMap.Builder<String, String> proxySettingsBuilder = ImmutableMap.<String, String>builder()
                            .put(ProxyHelper.PROXY_SETTINGS_MOTDTOPLINE_KEY, proxySettings.getMotdTopLine())
                            .put(ProxyHelper.PROXY_SETTINGS_MOTDBOTTOMLINE_KEY, proxySettings.getMotdBottomLine())
                            .put(ProxyHelper.PROXY_SETTINGS_MOTDCENTERED_KEY, value);

                    jedis.hmset(ProxyHelper.PROXY_STORAGE_KEY + ":" + ProxyHelper.PROXY_SETTINGS_KEY, proxySettingsBuilder.build());
                }

                packetUtil.send(new ProxyUpdatePacket());
            });

            commandSender.sendMessage(TextComponent.fromLegacyText(Constants.MOTD_SUCCESS_CHANGE));
            return true;
        } else {
            commandSender.sendMessage(TextComponent.fromLegacyText(Constants.MOTD_TRUE_FALSE_CENTERED));
        }

        return false;
    }

}
