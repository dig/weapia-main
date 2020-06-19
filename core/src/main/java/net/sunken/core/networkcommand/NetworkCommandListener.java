package net.sunken.core.networkcommand;

import net.sunken.common.inject.*;
import net.sunken.common.networkcommand.*;
import net.sunken.common.packet.*;
import net.sunken.common.player.*;
import net.sunken.common.player.module.*;
import net.sunken.common.util.cooldown.*;
import net.sunken.core.Constants;
import net.sunken.core.util.*;
import org.apache.commons.lang.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

import javax.inject.*;

public class NetworkCommandListener implements Listener, Facet {

    private static final long GLOBAL_COOLDOWN_MILLIS = 100L;
    private static final String COOLDOWN_KEY = "networkcmd";

    @Inject
    private PlayerManager playerManager;
    @Inject
    private PacketUtil packetUtil;
    @Inject
    private Cooldowns cooldowns;
    @Inject
    private AvailableCommandsCache availableCommands;

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String commandString = event.getMessage();

        String[] splitByWord = commandString.split(" ");
        if (splitByWord.length != 0) {
            String firstWord = splitByWord[0];
            if (firstWord.length() > 1) {
                String commandName = firstWord.substring(1);

                CommandUtil.getCommandMap().ifPresent(commandMap -> {
                    boolean isRegisteredOnMaster = availableCommands.getAvailableCommands().contains(commandName);

                    if (commandMap.getCommand(commandName) == null && isRegisteredOnMaster) {
                        event.setCancelled(true);
                        playerManager.get(player.getUniqueId())
                                .map(AbstractPlayer::toPlayerDetail)
                                .ifPresent(playerDetail -> {
                                    if (!cooldowns.canProceed(COOLDOWN_KEY, player.getUniqueId())) {
                                        player.sendMessage(Constants.NETWORKCOMMAND_COOLDOWN);
                                        return;
                                    }
                                    cooldowns.create(COOLDOWN_KEY, player.getUniqueId(), System.currentTimeMillis() + GLOBAL_COOLDOWN_MILLIS);

                                    String[] args = splitByWord.length > 1 ?
                                            (String[]) ArrayUtils.subarray(splitByWord, 1, splitByWord.length) :
                                            new String[]{};

                                    NetworkCommandPacket networkCommand =
                                            new NetworkCommandPacket(commandName, args, playerDetail);
                                    packetUtil.send(networkCommand);
                                });
                    }
                });
            }
        }
    }
}
