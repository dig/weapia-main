package net.sunken.lobby.inventory;

import com.google.inject.Inject;
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
import net.sunken.core.executor.BukkitSyncExecutor;
import net.sunken.core.inventory.ItemBuilder;
import net.sunken.core.inventory.Page;
import net.sunken.core.inventory.PageContainer;
import net.sunken.core.inventory.element.Action;
import net.sunken.core.inventory.element.Element;
import net.sunken.core.inventory.element.ElementFactory;
import net.sunken.core.util.nbt.NBTItem;
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

public class LobbySelectorHandler implements Facet, Enableable, Listener, SunkenListener {

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
        Element darkGreenGlassPane = elementFactory.createElement(new ItemBuilder(Material.STAINED_GLASS_PANE)
                .name(ChatColor.WHITE + " ")
                .durability(13)
                .make());
        Element greenGlassPane = elementFactory.createElement(new ItemBuilder(Material.STAINED_GLASS_PANE)
                .name(ChatColor.WHITE + " ")
                .durability(5)
                .make());

        Page lobbyMainMenu = Page.builder()
                .id("lobby-main-menu")
                .title("Minevasion \u2996 Lobbies")
                .size(54)
                .putElement(0, darkGreenGlassPane)
                .putElement(1, greenGlassPane)
                .putElement(7, greenGlassPane)
                .putElement(8, darkGreenGlassPane)
                .putElement(9, greenGlassPane)
                .putElement(17, greenGlassPane)
                .putElement(36, greenGlassPane)
                .putElement(44, greenGlassPane)
                .putElement(45, darkGreenGlassPane)
                .putElement(46, greenGlassPane)
                .putElement(52, greenGlassPane)
                .putElement(53, darkGreenGlassPane)
                .build();

        container.add(lobbyMainMenu);
        container.setInitial(lobbyMainMenu);

        update(Server.Type.LOBBY);
    }

    @Override
    public void disable() {
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
                    .filter(element -> element.getItem().getType() == Material.BED)
                    .filter(element -> new NBTItem(element.getItem()).hasKey("serverId"))
                    .forEach(element -> {
                        ItemStack item = element.getItem();

                        NBTItem nbtItem = new NBTItem(item);
                        String serverId = nbtItem.getString("serverId");
                        Optional<Server> serverOptional = serverManager.findServerById(serverId);

                        if (serverOptional.isPresent()) {
                            Server server = serverOptional.get();
                            String metadataId = (server.getMetadata().containsKey(ServerHelper.SERVER_METADATA_ID_KEY) ? server.getMetadata().get(ServerHelper.SERVER_METADATA_ID_KEY) : "Pending");

                            List<String> lore = new ArrayList<>();
                            for (String line : uiConfiguration.getLobbySelectorTemplate().getLore())
                                lore.add(ChatColor.translateAlternateColorCodes('&', line.replaceAll("%players", String.valueOf(server.getPlayers()))));

                            ItemMeta itemMeta = item.getItemMeta();
                            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', uiConfiguration.getLobbySelectorTemplate().getDisplayName().replaceAll("%num", metadataId)));
                            itemMeta.setLore(lore);

                            item.setItemMeta(itemMeta);
                        }
                    });

            bukkitSyncExecutor.execute(() -> lobbyMainMenu.updateInventory());
        }
    }

    private ItemStack createLobbyItem(Server server) {
        ItemConfiguration lobbySelectorTemplate = uiConfiguration.getLobbySelectorTemplate();
        String metadataId = (server.getMetadata().containsKey(ServerHelper.SERVER_METADATA_ID_KEY) ? server.getMetadata().get(ServerHelper.SERVER_METADATA_ID_KEY) : "Pending");

        List<String> lore = new ArrayList<>();
        for (String line : lobbySelectorTemplate.getLore())
            lore.add(ChatColor.translateAlternateColorCodes('&', line.replaceAll("%players", String.valueOf(server.getPlayers()))));

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

                    Optional<Server> serverOptional = serverManager.findServerById(serverId);
                    if (serverOptional.isPresent()) {
                        packetUtil.send(new PlayerSendToServerPacket(observer.getUniqueId(), serverOptional.get().toServerDetail()));
                        observer.closeInventory();
                    }

                    return context;
                }));

                y++;
            }

            bukkitSyncExecutor.execute(() -> lobbyMainMenu.updateInventory());
        }
    }

}