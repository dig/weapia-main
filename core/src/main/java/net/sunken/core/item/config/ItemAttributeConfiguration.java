package net.sunken.core.item.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@Getter
@AllArgsConstructor
public class ItemAttributeConfiguration {

    private String key;
    private Object value;

}
