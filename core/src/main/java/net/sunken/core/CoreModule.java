package net.sunken.core;

import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import net.sunken.common.CommonModule;
import net.sunken.common.config.ConfigModule;
import net.sunken.common.inject.FacetBinder;
import net.sunken.core.command.CommandModule;
import net.sunken.core.config.InstanceConfiguration;
import net.sunken.core.config.InstanceConfigurationSerializer;
import net.sunken.core.hologram.HologramListener;
import net.sunken.core.inventory.command.ExampleInvCommand;
import net.sunken.core.inventory.element.ElementListener;
import net.sunken.core.item.ItemListener;
import net.sunken.core.item.ItemRegistry;
import net.sunken.core.item.command.GiveItemCommand;
import net.sunken.core.item.command.ItemCommand;
import net.sunken.core.item.config.ItemAttributeConfiguration;
import net.sunken.core.item.config.ItemAttributeConfigurationSerializer;
import net.sunken.core.networkcommand.*;
import net.sunken.core.npc.NPCPacketAdapter;
import net.sunken.core.npc.NPCRegistry;
import net.sunken.core.player.ChatHandler;
import net.sunken.core.player.ConnectHandler;
import net.sunken.core.player.DisconnectHandler;
import net.sunken.core.player.PlayerSaveStateHandler;
import net.sunken.core.scoreboard.command.NametagCommand;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

import java.io.File;

public class CoreModule extends AbstractModule {

    @Override
    protected void configure() {
        configureTypeSerializers();

        install(new CommonModule());
        install(new ConfigModule(new File("config/common.conf"), InstanceConfiguration.class));
        install(new CommandModule());
        install(new NetworkCommandModule());

        final FacetBinder facetBinder = new FacetBinder(binder());
        facetBinder.addBinding(ChatHandler.class);
        facetBinder.addBinding(ConnectHandler.class);
        facetBinder.addBinding(DisconnectHandler.class);
        facetBinder.addBinding(HologramListener.class);

        facetBinder.addBinding(PluginInform.class);
        facetBinder.addBinding(NPCRegistry.class);
        facetBinder.addBinding(NPCPacketAdapter.class);
        facetBinder.addBinding(ElementListener.class);
        facetBinder.addBinding(ExampleInvCommand.class);
        facetBinder.addBinding(NametagCommand.class);

        facetBinder.addBinding(ItemRegistry.class);
        facetBinder.addBinding(ItemListener.class);
        facetBinder.addBinding(ItemCommand.class);
        facetBinder.addBinding(GiveItemCommand.class);

        facetBinder.addBinding(PlayerSaveStateHandler.class);
    }

    private void configureTypeSerializers() {
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(InstanceConfiguration.class), new InstanceConfigurationSerializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(ItemAttributeConfiguration.class), new ItemAttributeConfigurationSerializer());
    }
}
