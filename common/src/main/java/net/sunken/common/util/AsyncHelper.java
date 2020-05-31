package net.sunken.common.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AsyncHelper {

    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public static ExecutorService executor() {
        return executorService;
    }
    public static ScheduledExecutorService scheduledExecutor() { return scheduledExecutorService; };

    private AsyncHelper() {
        throw new AssertionError("You must not attempt to instantiate this class.");
    }

}
