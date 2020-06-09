package net.sunken.core.item.config;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ItemAttributeConfigurationSerializer implements TypeSerializer<ItemAttributeConfiguration> {

    @Nullable
    @Override
    public ItemAttributeConfiguration deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        String key = value.getNode("key").getString();
        AttributeType attributeType = AttributeType.valueOf(value.getNode("type").getString());

        Object valueResult = null;
        switch (attributeType) {
            case STRING:
                valueResult = value.getNode("value").getString();
                break;
            case INTEGER:
                valueResult = value.getNode("value").getInt();
                break;
            case DOUBLE:
                valueResult = value.getNode("value").getDouble();
                break;
            case BOOLEAN:
                valueResult = value.getNode("value").getBoolean();
                break;
        }

        return new ItemAttributeConfiguration(key, valueResult);
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable ItemAttributeConfiguration obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
    }

}
