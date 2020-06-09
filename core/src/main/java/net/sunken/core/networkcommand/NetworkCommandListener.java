package net.sunken.core.command;

import net.sunken.common.inject.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class NetworkCommandListener implements Listener, Facet {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();

        String[] splitByWord = message.split(" ");
        if (splitByWord.length != 0) {
            String firstWord = splitByWord[0];
            if (firstWord.length() > 1) {
                String commandName = firstWord.substring(1);
                String fullCommand = message.substring(1);

                
            }
        }
    }
}
