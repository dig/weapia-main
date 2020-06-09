package net.sunken.core.networkcommand;

import net.sunken.common.inject.*;
import net.sunken.common.networkcommand.*;
import net.sunken.common.packet.*;
import net.sunken.common.player.*;
import net.sunken.common.player.module.*;
import net.sunken.core.util.*;
import org.apache.commons.lang.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

import javax.inject.*;
import java.util.*;

public class NetworkCommandListener implements Listener, Facet {

    @Inject
    private PlayerManager playerManager;
    @Inject
    private PacketUtil packetUtil;

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        String message = event.getMessage();

        String[] splitByWord = message.split(" ");
        if (splitByWord.length != 0) {
            String firstWord = splitByWord[0];
            if (firstWord.length() > 1) {
                String commandName = firstWord.substring(1);

                CommandUtil.findSimpleCommandMap().ifPresent(commandMap -> {
                    if (commandMap.getCommand(commandName) == null) {
                        playerManager.get(playerId)
                                .map(AbstractPlayer::toPlayerDetail)
                                .ifPresent(playerDetail -> {
                                    String[] args = splitByWord.length > 1 ?
                                            (String[]) ArrayUtils.subarray(splitByWord, 1, splitByWord.length) :
                                            new String[]{};

                                    NetworkCommandPacket networkCommandPacket =
                                            new NetworkCommandPacket(commandName, args, playerDetail);
                                    packetUtil.send(networkCommandPacket);
                                });
                    }
                });
            }
        }
    }
}
