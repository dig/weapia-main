package net.sunken.bungeecord.command;

import com.google.inject.Inject;
import net.sunken.common.command.impl.BaseCommandRegistry;
import net.sunken.common.player.module.PlayerManager;
import net.sunken.common.util.cooldown.Cooldowns;

public class CommandHandlerFactory {

    @Inject
    private BaseCommandRegistry baseCommandRegistry;
    @Inject
    private PlayerManager playerManager;
    @Inject
    private Cooldowns cooldowns;

    public CommandHandler createHandler(String name, String[] aliases) {
        return new CommandHandler(name, aliases, baseCommandRegistry, playerManager, cooldowns);
    }

}
