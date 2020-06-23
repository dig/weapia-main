package net.sunken.common.config;

import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

@Log
@AllArgsConstructor
public class ConfigModule extends AbstractModule {

    private File configFile;
    private Class configClass;

    @Override
    protected void configure() {
        if (configFile.exists()) {
            ConfigurationLoader<CommentedConfigurationNode> loader =
                    HoconConfigurationLoader.builder().setPath(configFile.toPath()).build();

            try {
                ConfigurationNode rootNode = loader.load();

                if (rootNode != null)
                    bind(configClass)
                            .annotatedWith(InjectConfig.class)
                            .toInstance(rootNode.getValue(TypeToken.of(configClass)));
            } catch (IOException | ObjectMappingException e) {
                log.log(Level.SEVERE, String.format("Error while loading config (%s)", configFile.getName()), e);
            }
        } else {
            log.severe(String.format("Unable to find config. (%s, %s)", configFile.getName(), configFile.getAbsolutePath()));
        }
    }
}
