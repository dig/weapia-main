package net.sunken.core.executor;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executor;

public class BukkitSyncExecutor implements Executor {

    @Inject
    private JavaPlugin plugin;

    @Override
    public void execute(Runnable command) {
        Preconditions.checkNotNull(command, "Null runnable.");
        Bukkit.getScheduler().runTask(plugin, command);
    }

    public void execute(Runnable command, long delay) {
        Preconditions.checkNotNull(command, "Null runnable.");
        Bukkit.getScheduler().runTaskLater(plugin, command, delay);
    }

    public void execute(Runnable command, long delay, long period) {
        Preconditions.checkNotNull(command, "Null runnable.");
        Bukkit.getScheduler().runTaskTimer(plugin, command, delay, period);
    }

}
