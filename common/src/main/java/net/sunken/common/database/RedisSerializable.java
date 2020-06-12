package net.sunken.common.database;

import java.util.Map;

public interface RedisSerializable {

    Map<String, String> toRedis();

}
