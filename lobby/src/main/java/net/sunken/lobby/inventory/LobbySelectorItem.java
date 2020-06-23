package net.sunken.lobby.inventory;

import com.google.inject.Inject;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.sunken.common.config.InjectConfig;
import net.sunken.common.event.ListensToEvent;
import net.sunken.common.event.SunkenListener;
import net.sunken.common.inject.Enableable;
import net.sunken.common.inject.Facet;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.packet.PlayerSendToServerPacket;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import net.sunken.common.server.ServerHelper;
import net.sunken.common.server.module.ServerManager;
import net.sunken.common.server.module.event.ServerAddedEvent;
import net.sunken.common.server.module.event.ServerRemovedEvent;
import net.sunken.common.server.module.event.ServerUpdatedEvent;
import net.sunken.common.util.AsyncHelper;
import net.sunken.core.executor.BukkitSyncExecutor;
import net.sunken.core.inventory.ItemBuilder;
import net.sunken.core.inventory.Page;
import net.sunken.core.inventory.PageContainer;
import net.sunken.core.inventory.element.Action;
import net.sunken.core.inventory.element.Element;
import net.sunken.core.inventory.element.ElementFactory;
import net.sunken.lobby.config.ItemConfiguration;
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

public class LobbySelectorItem implements Facet, Enableable, Listener, SunkenListener {

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

        Page lobbyMainMenu = Page.builder()
                .id("lobby-main-menu")
                .title("Weapia > Lobbies")
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

        container.add(lobbyMainMenu);
        container.setInitial(lobbyMainMenu);
        update(Server.Type.LOBBY);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ItemStack lobbySelector = elementFactory.createActionableElement(net.sunken.lobby.Constants.ITEM_LOBBY_SELECTOR.make(), Action.BOTH, context -> {
            Player observer = context.getObserver();
            container.launchFor(observer);
            return context;
        }).getItem();
        player.getInventory().setItem(5, lobbySelector);
    }

    @ListensToEvent
    public void onServerAdded(ServerAddedEvent event) {
        update(event.getServer().getType());
    }

    @ListensToEvent
    public void onServerRemoved(ServerRemovedEvent event) {
        update(event.getServer().getType());
    }

    @ListensToEvent
    public void onServerUpdated(ServerUpdatedEvent event) {
        Page lobbyMainMenu = container.getPages().get("lobby-main-menu");

        if (event.getServer().getType() == Server.Type.LOBBY) {
            lobbyMainMenu.getElements().values().stream()
                    .filter(element -> element.getItem().getType() == Material.GRAY_BED)
                    .map(element -> new NBTItem(element.getItem()))
                    .filter(nbtItem -> nbtItem.hasKey("serverId"))
                    .forEach(nbtItem -> {
                        ItemStack item = nbtItem.getItem();
                        String serverId = nbtItem.getString("serverId");

                        serverManager.findServerById(serverId)
                                .ifPresent(server -> {
                                    String metadataId = (server.getMetadata().containsKey(ServerHelper.SERVER_METADATA_ID_KEY) ? server.getMetadata().get(ServerHelper.SERVER_METADATA_ID_KEY) : "Pending");
                                    List<String> lore = uiConfiguration.getLobbySelectorTemplate().getLore().stream()
                                            .map(s -> ChatColor.translateAlternateColorCodes('&', s.replaceAll("%players", String.valueOf(server.getPlayers()))))
                                            .collect(Collectors.toList());

                                    ItemMeta itemMeta = item.getItemMeta();
                                    itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', uiConfiguration.getLobbySelectorTemplate().getDisplayName().replaceAll("%num", metadataId)));
                                    itemMeta.setLore(lore);

                                    item.setItemMeta(itemMeta);
                                });
                    });

            bukkitSyncExecutor.execute(() -> lobbyMainMenu.updateInventory());
        }
    }

    private ItemStack createLobbyItem(Server server) {
        ItemConfiguration lobbySelectorTemplate = uiConfiguration.getLobbySelectorTemplate();
        String metadataId = (server.getMetadata().containsKey(ServerHelper.SERVER_METADATA_ID_KEY) ? server.getMetadata().get(ServerHelper.SERVER_METADATA_ID_KEY) : "Pending");

        List<String> lore = lobbySelectorTemplate.getLore().stream()
                .map(s -> ChatColor.translateAlternateColorCodes('&', s.replaceAll("%players", String.valueOf(server.getPlayers()))))
                .collect(Collectors.toList());

        ItemBuilder serverItemBuilder = new ItemBuilder(lobbySelectorTemplate.getMaterial())
                .name(ChatColor.translateAlternateColorCodes('&', lobbySelectorTemplate.getDisplayName().replaceAll("%num", metadataId)))
                .lores(lore)
                .addNBTString("serverId", server.getId())
                .durability(lobbySelectorTemplate.getDurability());

        return serverItemBuilder.make();
    }

    private void update(Server.Type type) {
        if (type == Server.Type.LOBBY) {
            Page lobbyMainMenu = container.getPages().get("lobby-main-menu");

            for (int i = 0; i < 7; i++) {
                lobbyMainMenu.removeElement(19 + i);
                lobbyMainMenu.removeElement(28 + i);
            }

            int y = 0;
            for (Server server : serverManager.findAll(Server.Type.LOBBY, Game.NONE)) {
                lobbyMainMenu.getElements().put((y > 6 ? 21 : 19) + y, elementFactory.createActionableElement(createLobbyItem(server), context -> {
                    Player observer = context.getObserver();
                    NBTItem nbtItem = new NBTItem(context.getItem());
                    String serverId = nbtItem.getString("serverId");

                    serverManager.findServerById(serverId)
                            .ifPresent(srv -> {
                                AsyncHelper.executor().submit(() -> packetUtil.send(new PlayerSendToServerPacket(observer.getUniqueId(), srv.toServerDetail())));
                                observer.closeInventory();
                            });

                    return context;
                }));

                y++;
            }

            bukkitSyncExecutor.execute(() -> lobbyMainMenu.updateInventory());
        }
    }
}