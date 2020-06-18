package net.sunken.core;

import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import net.sunken.common.CommonModule;
import net.sunken.common.config.ConfigModule;
import net.sunken.common.inject.PluginFacetBinder;
import net.sunken.common.player.module.PlayerModule;
import net.sunken.core.bar.module.BarModule;
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
import net.sunken.core.npc.NPCRegistry;
import net.sunken.core.player.ChatHandler;
import net.sunken.core.player.ConnectHandler;
import net.sunken.core.player.DisconnectHandler;
import net.sunken.core.scoreboard.command.NametagCommand;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

import java.io.File;

public class CoreModule extends AbstractModule {

    @Override
    protected void configure() {
        configureTypeSerializers();

        install(new CommonModule());
        install(new PlayerModule());
        install(new ConfigModule(new File("config/common.conf"), InstanceConfiguration.class));
        install(new CommandModule());
        install(new BarModule());
        // install(new NetworkCommandModule());

        final PluginFacetBinder pluginFacetBinder = new PluginFacetBinder(binder());
        pluginFacetBinder.addBinding(ChatHandler.class);
        pluginFacetBinder.addBinding(ConnectHandler.class);
        pluginFacetBinder.addBinding(DisconnectHandler.class);
        pluginFacetBinder.addBinding(HologramListener.class);

        pluginFacetBinder.addBinding(PluginInform.class);
        pluginFacetBinder.addBinding(NPCRegistry.class);
        pluginFacetBinder.addBinding(ElementListener.class);
        pluginFacetBinder.addBinding(ExampleInvCommand.class);
        pluginFacetBinder.addBinding(NametagCommand.class);

        pluginFacetBinder.addBinding(ItemRegistry.class);
        pluginFacetBinder.addBinding(ItemListener.class);
        pluginFacetBinder.addBinding(ItemCommand.class);
        pluginFacetBinder.addBinding(GiveItemCommand.class);
    }

    private void configureTypeSerializers() {
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(InstanceConfiguration.class), new InstanceConfigurationSerializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(ItemAttributeConfiguration.class), new ItemAttributeConfigurationSerializer());
    }

}
