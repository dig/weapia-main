package net.sunken.core.engine.state.impl;

import com.google.common.reflect.TypeToken;
import com.google.inject.*;
import lombok.extern.java.Log;
import net.sunken.common.packet.PacketUtil;
import net.sunken.common.player.PlayerManager;
import net.sunken.core.PluginInform;
import net.sunken.core.engine.*;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.File;
import java.io.IOException;

@Log
public abstract class BaseState {

    @Inject
    protected EngineManager engineManager;
    @Inject
    protected PlayerManager playerManager;
    @Inject
    protected PluginInform pluginInform;
    @Inject
    protected PacketUtil packetUtil;

    //--- Called on state start.
    public abstract void start(BaseGameState previous);

    //--- Called on state stop, before switching.
    public abstract void stop(BaseGameState next);

    public <T> T loadConfig(String filePath, Class<T> type) {
        File configFile = new File(filePath);
        ConfigurationLoader<CommentedConfigurationNode> loader =
                HoconConfigurationLoader.builder().setPath(configFile.toPath()).build();

        try {
            ConfigurationNode rootNode = loader.load();
            return rootNode.getValue(TypeToken.of(type));
        } catch (IOException | ObjectMappingException e) {
            log.severe(String.format("Unable to load world config file. (%s)", configFile.getName()));
        }

        return null;
    }

}
