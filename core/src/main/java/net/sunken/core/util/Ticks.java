package net.sunken.core.util;

import lombok.experimental.*;

import java.util.concurrent.*;

@UtilityClass
public class Ticks {

    // the number of ticks which occur in a second - this is a server implementation detail
    private static final int TICKS_PER_SECOND = 20;
    // the number of milliseconds in a second - constant
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // the number of milliseconds in a tick - assuming the server runs at a perfect tick rate
    private static final int MILLISECONDS_PER_TICK = MILLISECONDS_PER_SECOND / TICKS_PER_SECOND;

    public static long from(long duration, TimeUnit unit) {
        return unit.toMillis(duration) / MILLISECONDS_PER_TICK;
    }

    public static long to(long ticks, TimeUnit unit) {
        return unit.convert(ticks * MILLISECONDS_PER_TICK, TimeUnit.MILLISECONDS);
    }
}
