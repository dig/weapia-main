package net.sunken.core.engine.command;

import com.google.inject.Inject;
import net.sunken.common.command.Command;
import net.sunken.common.config.InjectConfig;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.Rank;
import net.sunken.common.player.module.PlayerManager;
import net.sunken.common.server.module.ServerManager;
import net.sunken.core.command.BukkitCommand;
import net.sunken.core.engine.EngineManager;
import org.bukkit.command.CommandSender;

import java.util.Optional;

@Command(aliases = {"debug"}, rank = Rank.DEVELOPER)
public class DebugCommand extends BukkitCommand {

    @Inject
    private EngineManager engineManager;
    @Inject
    private ServerManager serverManager;
    @Inject
    private PlayerManager playerManager;

    @Override
    public boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        commandSender.sendMessage("---- ENGINE MANAGER ----");
        commandSender.sendMessage(String.format("State: %s", engineManager.getCurrentGameState().getClass().getSimpleName()));
        commandSender.sendMessage(String.format("GameMode: %s", engineManager.getGameMode().toString()));

        commandSender.sendMessage("---- PLAYER MANAGER ----");
        commandSender.sendMessage(String.format("onlinePlayers count: %s", playerManager.getOnlinePlayers().size()));
        commandSender.sendMessage(String.format("onlinePlayers: %s", playerManager.getOnlinePlayers().toString()));
        return true;
    }

}
