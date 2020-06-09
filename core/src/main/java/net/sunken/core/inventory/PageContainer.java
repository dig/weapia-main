package net.sunken.core.inventory;

import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.entity.Player;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

public class PageContainer {

    @Getter
    private String initialPage;
    @Getter
    private final Map<String, Page> pages = Maps.newHashMap();

    public void add(Page page) {
        pages.put(page.getId(), page);
    }

    public void setInitial(Page page) {
        initialPage = page.getId();

        if (!pages.containsKey(page.getId())) {
            this.add(page);
        }
    }

    public void launchFor(Player player) {
        this.open(player, initialPage);
    }

    public void open(Player player, String pageId) {
        checkState(pages.containsKey(pageId), "there is no page that exists with that ID");

        Page page = pages.get(pageId);
        page.updateInventory();

        player.openInventory(page.getInventory());
    }

}
