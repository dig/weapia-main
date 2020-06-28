package net.sunken.lobby.inventory;

import com.google.inject.Inject;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.sunken.common.config.InjectConfig;
import net.sunken.common.event.ListensToEvent;
import net.sunken.common.event.SunkenListener;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.packet.PlayerRequestServerPacket;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import net.sunken.common.server.module.ServerManager;
import net.sunken.common.server.module.event.ServerAddedEvent;
import net.sunken.common.server.module.event.ServerRemovedEvent;
import net.sunken.common.server.module.event.ServerUpdatedEvent;
import net.sunken.common.util.AsyncHelper;
import net.sunken.common.util.Tuple;
import net.sunken.core.Constants;
import net.sunken.core.executor.BukkitSyncExecutor;
import net.sunken.core.inventory.ItemBuilder;
import net.sunken.core.inventory.Page;
import net.sunken.core.inventory.PageContainer;
import net.sunken.core.inventory.element.Action;
import net.sunken.core.inventory.element.Element;
import net.sunken.core.inventory.element.ElementFactory;
import net.sunken.lobby.config.SelectorInteractionConfiguration;
import net.sunken.lobby.config.SelectorItemConfiguration;
import net.sunken.lobby.config.UIConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GameSelectorItem implements Facet, Enableable, Listener, SunkenListener {

    @Inject @InjectConfig
    private UIConfiguration uiConfiguration;
    @Inject
    private ElementFactory elementFactory;
    @Inject
    private ServerManager serverManager;
    @Inject
    private PacketUtil packetUtil;
    @Inject
    private BukkitSyncExecutor bukkitSyncExecutor;

    @Inject
    private PageContainer container;

    @Override
    public void enable() {
        Element darkAquaGlassPane = elementFactory.createElement(new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE)
                .name(ChatColor.WHITE + " ")
                .make());
        Element aquaGlassPane = elementFactory.createElement(new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
                .name(ChatColor.WHITE + " ")
                .make());

        Page compassMainMenu = Page.builder()
                .id("compass-main-menu")
                .title("Weapia > Choose Game")
                .size(54)
                .putElement(0, darkAquaGlassPane)
                .putElement(1, aquaGlassPane)
                .putElement(7, aquaGlassPane)
                .putElement(8, darkAquaGlassPane)
                .putElement(9, aquaGlassPane)
                .putElement(17, aquaGlassPane)
                .putElement(36, aquaGlassPane)
                .putElement(44, aquaGlassPane)
                .putElement(45, darkAquaGlassPane)
                .putElement(46, aquaGlassPane)
                .putElement(52, aquaGlassPane)
                .putElement(53, darkAquaGlassPane)
                .build();

        for (SelectorItemConfiguration selectorItemConfiguration : uiConfiguration.getGameSelector()) {
            int count = serverManager.getPlayersOnline(selectorItemConfiguration.getServer().getType(), selectorItemConfiguration.getServer().getGame());

            List<String> lore = selectorItemConfiguration.getLore().stream()
                    .map(s -> ChatColor.translateAlternateColorCodes('&', s.replaceAll("%players", String.format("%,d", count))))
                    .collect(Collectors.toList());

            ItemBuilder selectorItemBuilder = new ItemBuilder(selectorItemConfiguration.getMaterial())
                    .name(ChatColor.translateAlternateColorCodes('&', selectorItemConfiguration.getDisplayName()))
                    .lores(lore)
                    .durability(selectorItemConfiguration.getDurability())
                    .addNBTString("id", selectorItemConfiguration.getId())
                    .addNBTString("type", selectorItemConfiguration.getServer().getType().toString())
                    .addNBTString("game", selectorItemConfiguration.getServer().getGame().toString());

            compassMainMenu.getElements().put(selectorItemConfiguration.getSlot(), elementFactory.createActionableElement(selectorItemBuilder.make(), context -> {
                Player observer = context.getObserver();

                SelectorInteractionConfiguration selectorInteractionConfiguration = selectorItemConfiguration.getInteraction();
                switch (selectorInteractionConfiguration.getType()) {
                    case QUEUE:
                        Server.Type type = Server.Type.valueOf(selectorInteractionConfiguration.getData().get(0));
                        Game game = Game.valueOf(selectorInteractionConfiguration.getData().get(1));

                        observer.sendMessage(String.format(Constants.SEND_TO_GAME, game.getFriendlyName()));
                        AsyncHelper.executor().submit(() -> packetUtil.send(new PlayerRequestServerPacket(observer.getUniqueId(),type, game, true)));
                        break;
                    case MESSAGE:
                        selectorInteractionConfiguration.getData().forEach(message -> observer.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
                        break;
                }

                observer.closeInventory();
                return context;
            }));
        }

        container.add(compassMainMenu);
        container.setInitial(compassMainMenu);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ItemStack gameSelector = elementFactory.createActionableElement(net.sunken.lobby.Constants.ITEM_GAME_SELECTOR.make(), Action.BOTH, context -> {
            Player observer = context.getObserver();
            container.launchFor(observer);
            return context;
        }).getItem();

        player.getInventory().setItem(4, gameSelector);
        player.getInventory().setHeldItemSlot(4);
    }

    @ListensToEvent
    public void onServerAdded(ServerAddedEvent event) {
        update(event.getServer());
    }

    @ListensToEvent
    public void onServerUpdated(ServerUpdatedEvent event) {
        update(event.getServer());
    }

    @ListensToEvent
    public void onServerRemoved(ServerRemovedEvent event) {
        update(event.getServer());
    }

    private void update(Server server) {
        Page compassMainMenu = container.getPages().get("compass-main-menu");
        compassMainMenu.getElements().values().stream()
                .map(element -> new Tuple<>(element, new NBTItem(element.getItem())))
                .filter(itemTuple -> itemTuple.getY().hasKey("type") && itemTuple.getY().hasKey("game"))
                .filter(itemTuple -> Server.Type.valueOf(itemTuple.getY().getString("type")) == server.getType())
                .filter(itemTuple -> Game.valueOf(itemTuple.getY().getString("game")) == server.getGame())
                .forEach(itemTuple -> {
                    ItemStack item = itemTuple.getX().getItem();
                    NBTItem nbtItem = itemTuple.getY();

                    Optional<SelectorItemConfiguration> selectorItemConfigurationOptional = uiConfiguration.getGameSelector().stream()
                            .filter(itemConfiguration -> itemConfiguration.getId().equals(nbtItem.getString("id")))
                            .findFirst();

                    selectorItemConfigurationOptional.ifPresent(selectorItemConfiguration -> {
                        int count = serverManager.getPlayersOnline(server.getType(), server.getGame());

                        ItemMeta itemMeta = item.getItemMeta();
                        List<String> lore = selectorItemConfiguration.getLore().stream()
                                .map(s -> ChatColor.translateAlternateColorCodes('&', s.replaceAll("%players", String.format("%,d", count))))
                                .collect(Collectors.toList());

                        itemMeta.setLore(lore);
                        item.setItemMeta(itemMeta);
                    });
                });

        bukkitSyncExecutor.execute(() -> compassMainMenu.updateInventory());
    }
}
