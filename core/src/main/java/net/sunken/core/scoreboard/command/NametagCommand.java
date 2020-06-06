package net.sunken.core.scoreboard.command;

import com.google.inject.Inject;
import net.sunken.common.command.Command;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.Rank;
import net.sunken.core.Constants;
import net.sunken.core.command.BukkitCommand;
import net.sunken.core.scoreboard.CustomNameDetail;
import net.sunken.core.scoreboard.ScoreboardRegistry;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Optional;

@Command(aliases = {"nametag"}, rank = Rank.DEVELOPER, usage = "/nametag <prefix|suffix|colour> <value>", min = 2, max = 2)
public class NametagCommand extends BukkitCommand {

    @Inject
    private ScoreboardRegistry scoreboardRegistry;

    @Override
    public boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        if (abstractPlayerOptional.isPresent()) {
            AbstractPlayer abstractPlayer = abstractPlayerOptional.get();

            String prefix = "";
            String suffix = "";
            ChatColor colour = ChatColor.WHITE;
            int order = Rank.PLAYER.getOrder();

            Optional<CustomNameDetail> customNameDetailOptional = scoreboardRegistry.getCustomName(abstractPlayer.getUsername());
            if (customNameDetailOptional.isPresent()) {
                CustomNameDetail customNameDetail = customNameDetailOptional.get();
                prefix = customNameDetail.getPrefix();
                suffix = customNameDetail.getSuffix();
                colour = customNameDetail.getColour();
                order = customNameDetail.getOrder();
            }

            if (args[0].equalsIgnoreCase("prefix")) {
                prefix = ChatColor.translateAlternateColorCodes('&', args[1]);
            } else if (args[0].equalsIgnoreCase("suffix")) {
                suffix = ChatColor.translateAlternateColorCodes('&', args[1]);
            } else if (args[0].equalsIgnoreCase("colour")) {
                colour = ChatColor.valueOf(args[1]);
            }

            scoreboardRegistry.changeName(abstractPlayer.getUsername(), prefix, suffix, colour, order);
            commandSender.sendMessage(Constants.COMMAND_NAMETAG_SUCCESS);
            return true;
        }

        return false;
    }

}
