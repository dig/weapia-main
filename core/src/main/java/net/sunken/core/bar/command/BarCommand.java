package net.sunken.core.bar.command;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import net.sunken.common.command.Command;
import net.sunken.common.command.SubCommand;
import net.sunken.common.database.RedisConnection;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.Rank;
import net.sunken.common.util.AsyncHelper;
import net.sunken.core.Constants;
import net.sunken.core.bar.BarHelper;
import net.sunken.core.bar.BarSettings;
import net.sunken.core.bar.packet.BarUpdatePacket;
import net.sunken.core.command.BukkitCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import redis.clients.jedis.Jedis;

import java.util.Optional;

@Command(aliases = {"bar"}, rank = Rank.OWNER, usage = "/bar <action|boss|clear> [value]", min = 1)
public class BarCommand extends BukkitCommand {

    @Inject
    private RedisConnection redisConnection;
    @Inject
    private PacketUtil packetUtil;
    @Inject
    private BarSettings barSettings;

    @Override
    public boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        commandSender.sendMessage(ChatColor.RED + "Usage: /bar <action|boss|clear> [value]");
        return true;
    }

    @SubCommand(aliases = {"action"}, rank = Rank.OWNER, min = 1)
    public boolean action(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        String value = String.join(" ", args);

        AsyncHelper.executor().submit(() -> {
            try (Jedis jedis = redisConnection.getConnection()) {
                ImmutableMap.Builder<String, String> barSettingsBuilder = ImmutableMap.<String, String>builder()
                        .put(BarHelper.BAR_SETTINGS_ACTION_KEY, value)
                        .put(BarHelper.BAR_SETTINGS_BOSS_KEY, barSettings.getBossBar().getTitle());

                jedis.hmset(BarHelper.BAR_STORAGE_KEY + ":" + BarHelper.BAR_SETTINGS_KEY, barSettingsBuilder.build());
            }

            packetUtil.sendSync(new BarUpdatePacket());
        });

        commandSender.sendMessage(Constants.BAR_SUCCESS_CHANGE);
        return true;
    }

    @SubCommand(aliases = {"boss"}, rank = Rank.OWNER, min = 1)
    public boolean boss(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        String value = String.join(" ", args);

        AsyncHelper.executor().submit(() -> {
            try (Jedis jedis = redisConnection.getConnection()) {
                Optional<String> actionBarOptional = barSettings.getAction();

                ImmutableMap.Builder<String, String> barSettingsBuilder = ImmutableMap.<String, String>builder()
                        .put(BarHelper.BAR_SETTINGS_ACTION_KEY, (actionBarOptional.isPresent() ? actionBarOptional.get() : ""))
                        .put(BarHelper.BAR_SETTINGS_BOSS_KEY, value);

                jedis.hmset(BarHelper.BAR_STORAGE_KEY + ":" + BarHelper.BAR_SETTINGS_KEY, barSettingsBuilder.build());
            }

            packetUtil.sendSync(new BarUpdatePacket());
        });

        commandSender.sendMessage(Constants.BAR_SUCCESS_CHANGE);
        return true;
    }

    @SubCommand(aliases = {"clear"}, rank = Rank.OWNER)
    public boolean clear(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        AsyncHelper.executor().submit(() -> {
            try (Jedis jedis = redisConnection.getConnection()) {
                jedis.del(BarHelper.BAR_STORAGE_KEY + ":" + BarHelper.BAR_SETTINGS_KEY);
            }

            packetUtil.sendSync(new BarUpdatePacket());
        });

        commandSender.sendMessage(Constants.BAR_CLEARED);
        return true;
    }

}
