package net.sunken.common.database;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.NonNull;
import net.sunken.common.config.InjectConfig;
import net.sunken.common.database.config.RedisConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Singleton
public class RedisConnection extends Database<Jedis> {

    @Getter
    private JedisPool jedisPool;

    @Inject
    public RedisConnection(@InjectConfig RedisConfiguration redisConfiguration) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPool = new JedisPool(jedisPoolConfig, redisConfiguration.getHost(), redisConfiguration.getPort(), 0, redisConfiguration.getPassword());
    }

    @NonNull
    public Jedis getConnection() {
        return jedisPool.getResource();
    }

    @Override
    public void disconnect() { jedisPool.destroy(); }

}
