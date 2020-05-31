package net.sunken.core.inventory.runnable;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UIRunnableContext {

    @Getter
    private final Player observer;
    @Getter
    private final ItemStack item;
    @Getter @Setter
    private boolean cancelled = true;

    public UIRunnableContext(Player observer, ItemStack item) {
        this.observer = observer;
        this.item = item;
    }

}
