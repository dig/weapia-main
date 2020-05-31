package net.sunken.common.util;

import java.util.Random;

public class RandomUtil {

    private static final Random random = new Random();

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

}
