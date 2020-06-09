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
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

@Command(aliases = {"give"}, rank = Rank.ADMIN, usage = "/give <player> <id> [count]", min = 2, max = 3)
public class GiveItemCommand extends BukkitCommand {

    @Inject
    private ItemRegistry itemRegistry;

    @Override
    public boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        Player target = Bukkit.getPlayer(args[0]);

        if (target != null) {
            Optional<AnItem> anItemOptional = itemRegistry.getItem(args[1]);
            if (anItemOptional.isPresent()) {
                int count = 1;
                if (args.length > 2 && isInteger(args[2])) {
                    count = Integer.parseInt(args[2]);
                }

                for (int i = 0; i < count; i++) {
                    target.getInventory().addItem(anItemOptional.get().toItemStack());
                }

                commandSender.sendMessage(Constants.COMMAND_GIVE_ITEM_SUCCESS);
                return true;
            } else {
                commandSender.sendMessage(Constants.COMMAND_ITEM_ID_INVALID);
            }
        } else {
            commandSender.sendMessage(Constants.COMMAND_ITEM_TARGET_INVALID);
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
