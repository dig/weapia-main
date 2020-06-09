package net.sunken.core.item.command;

import com.google.inject.Inject;
import net.sunken.common.command.Command;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.Rank;
import net.sunken.core.Constants;
import net.sunken.core.command.BukkitCommand;
import net.sunken.core.item.ItemRegistry;
import net.sunken.core.item.impl.AnItem;
import net.sunken.core.player.CorePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

@Command(aliases = {"item"}, rank = Rank.ADMIN, usage = "/item <id> [count]", min = 1, max = 2)
public class ItemCommand extends BukkitCommand {

    @Inject
    private ItemRegistry itemRegistry;

    @Override
    public boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        if (abstractPlayerOptional.isPresent()) {
            CorePlayer corePlayer = (CorePlayer) abstractPlayerOptional.get();
            Optional<? extends Player> playerOptional = corePlayer.toPlayer();
            if (playerOptional.isPresent()) {
                Player player = playerOptional.get();

                Optional<AnItem> anItemOptional = itemRegistry.getItem(args[0]);
                if (anItemOptional.isPresent()) {
                    int count = 1;
                    if (args.length > 1 && isInteger(args[1])) {
                        count = Integer.parseInt(args[1]);
                    }

                    for (int i = 0; i < count; i++) {
                        player.getInventory().addItem(anItemOptional.get().toItemStack());
                    }

                    commandSender.sendMessage(Constants.COMMAND_ITEM_SUCCESS);
                    return true;
                } else {
                    commandSender.sendMessage(Constants.COMMAND_ITEM_ID_INVALID);
                }
            }

        }

        return false;
    }

    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(Exception e){
            return false;
        }

        return true;
    }

}
