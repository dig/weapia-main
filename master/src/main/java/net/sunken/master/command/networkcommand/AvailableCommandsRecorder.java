package net.sunken.master.command.networkcommand;

import lombok.extern.java.Log;
import net.sunken.common.command.impl.*;
import net.sunken.common.database.*;
import net.sunken.common.inject.*;
import net.sunken.common.networkcommand.*;
import redis.clients.jedis.*;

import javax.inject.*;
import java.util.*;

// Persists available commands to Redis when Master boots up so that servers can fetch these on startup
@Log
@Singleton
public class AvailableCommandsRecorder implements Facet, Enableable {

    @Inject
    private RedisConnection redisConnection;
    @Inject
    private BaseCommandRegistry commandRegistry;

    @Override
    public void enable() {
        try (Jedis connection = redisConnection.getJedisPool().getResource()) {
            // get rid of what was there before (master could reboot and possibly remove commands)
            connection.del(NetworkCommandConstants.COMMAND_LIST_KEY);

            Set<BaseCommand> registeredCommands = commandRegistry.getRegisteredCommands();
            Set<String> commandNames = new HashSet<>();
            registeredCommands.forEach(command -> commandNames.addAll(Arrays.asList(command.getCommand().aliases())));

            log.info(String.format("Adding commands to cache %s", commandNames.toString()));

            if (commandNames.size() > 0) {
                connection.sadd(NetworkCommandConstants.COMMAND_LIST_KEY, commandNames.toArray(new String[]{}));
            }
        }
    }

    @Override
    public void disable() {
    }
}
