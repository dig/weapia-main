package net.sunken.common.util;

import static redis.clients.jedis.ScanParams.SCAN_POINTER_START;
import com.google.common.collect.Sets;
import net.sunken.common.network.NetworkHelper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.Set;

public class RedisUtil {

    public static Set<String> scanAll(Jedis jedis, String pattern) {
        Set<String> keys = Sets.newHashSet();

        ScanParams params = new ScanParams();
        params.match(pattern);

        ScanResult<String> result;
        String cursor = SCAN_POINTER_START;

        do {
            result = jedis.scan(cursor, params);
            keys.addAll(result.getResult());
            cursor = result.getCursor();
        } while (!result.getCursor().equals(SCAN_POINTER_START));

        return keys;
    }

}
