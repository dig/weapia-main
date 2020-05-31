package net.sunken.core.inventory.command;

import com.google.inject.Inject;
import net.sunken.common.command.Command;
import net.sunken.common.player.AbstractPlayer;
import net.sunken.common.player.Rank;
import net.sunken.core.command.BukkitCommand;
import net.sunken.core.inventory.Page;
import net.sunken.core.inventory.PageContainer;
import net.sunken.core.inventory.element.ActionableElement;
import net.sunken.core.inventory.element.Element;
import net.sunken.core.inventory.element.ElementFactory;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@Command(aliases = {"exampleinv"}, rank = Rank.DEVELOPER)
public class ExampleInvCommand extends BukkitCommand {

    @Inject
    private PageContainer pageContainer;
    @Inject
    private ElementFactory elementFactory;

    @Override
    public boolean onCommand(CommandSender commandSender, Optional<AbstractPlayer> abstractPlayerOptional, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            // main menu page
            Page mainMenuPage = Page.builder()
                    .id("main-menu")
                    .title("Main menu of some kind")
                    .size(54)
                    .putElement(0, elementFactory.createElement(Material.BEDROCK))
                    .putElement(1, elementFactory.createActionableElement(new ItemStack(Material.REDSTONE), context -> {
                        Player observer = context.getObserver();
                        observer.sendMessage("yo");
                        return context;
                    }))
                    .build();
            pageContainer.add(mainMenuPage);

            Page anotherPage = Page.builder()
                    .id("another-menu")
                    .title("Some other shit page")
                    .size(54)
                    .putElement(0, elementFactory.createElement(Material.BEDROCK))
                    .putElement(1, elementFactory.createActionableElement(new ItemStack(Material.BLAZE_POWDER), context -> {
                        Player observer = context.getObserver();
                        observer.sendMessage("another page");
                        pageContainer.open(observer, "main-menu");
                        return context;
                    }))
                    .build();
            pageContainer.add(anotherPage);

            // set it as the page to launch with
            pageContainer.setInitial(mainMenuPage);

            // launch
            pageContainer.launchFor(player);

            return true;
        }

        return false;
    }

}
