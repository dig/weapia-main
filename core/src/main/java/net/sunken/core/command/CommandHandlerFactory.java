package net.sunken.core.command;

import com.google.inject.Inject;
import net.sunken.common.command.impl.BaseCommandRegistry;
import net.sunken.common.player.PlayerManager;
import net.sunken.common.util.cooldown.Cooldowns;

public class CommandHandlerFactory {

    @Inject
    private BaseCommandRegistry baseCommandRegistry;
    @Inject
    private PlayerManager playerManager;
    @Inject
    private Cooldowns cooldowns;

    public CommandHandler createHandler(String name) {
        return new CommandHandler(name, baseCommandRegistry, playerManager, cooldowns);
    }

}
