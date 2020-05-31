package net.sunken.core.config;

import com.google.common.reflect.TypeToken;
import net.sunken.common.server.Game;
import net.sunken.common.server.Server;
import net.sunken.common.server.World;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;

public class InstanceConfigurationSerializer implements TypeSerializer<InstanceConfiguration> {

    @Nullable @Override
    public InstanceConfiguration deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        String id = value.getNode("id").getString();
        Server.Type serverType = Server.Type.valueOf(value.getNode("type").getString());
        Game game = Game.valueOf(value.getNode("game").getString());
        World world = World.valueOf(value.getNode("world").getString());

        return new InstanceConfiguration(id, serverType, game, world, (!value.getNode("metadataId").isVirtual() ? Optional.of(value.getNode("metadataId").getString()) : Optional.empty()));
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable InstanceConfiguration obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        value.getNode("id").setValue(obj.getId());
        value.getNode("type").setValue(obj.getType().toString());
        value.getNode("game").setValue(obj.getGame().toString());
        value.getNode("world").setValue(obj.getWorld().toString());

        if (obj.getMetadataId().isPresent())
            value.getNode("metadataId").setValue(obj.getMetadataId().get());
    }

}
