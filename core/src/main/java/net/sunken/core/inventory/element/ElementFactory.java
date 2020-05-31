package net.sunken.core.inventory.element;

import com.google.inject.Inject;
import net.sunken.core.inventory.runnable.UIRunnable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ElementFactory {

    @Inject
    private ElementRegistry elementRegistry;

    public Element createElement(ItemStack item) {
        return new Element(item, elementRegistry);
    }

    public Element createElement(Material material) {
        return createElement(new ItemStack(material));
    }

    public ActionableElement createActionableElement(ItemStack item, UIRunnable runnable) {
        return new ActionableElement(item, runnable, elementRegistry);
    }

    public ActionableElement createActionableElement(ItemStack item, Action action, UIRunnable runnable) {
        return new ActionableElement(item, action, runnable, elementRegistry);
    }

}
