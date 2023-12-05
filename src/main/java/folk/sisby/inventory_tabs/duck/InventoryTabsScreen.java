package folk.sisby.inventory_tabs.duck;

import folk.sisby.inventory_tabs.util.WidgetPosition;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

import java.util.ArrayList;
import java.util.List;

public interface InventoryTabsScreen {
    boolean inventoryTabs$allowTabs();

    default List<WidgetPosition> getTabPositions(int tabWidth) {
        HandledScreen<?> screen = (HandledScreen<?>) this;
        List<WidgetPosition> list = new ArrayList<>();
        int count = screen.backgroundWidth / tabWidth;
        int margins = screen.backgroundWidth - tabWidth * count;

        for (int i = 0; i < count; i++) {
            list.add(new WidgetPosition(screen.x + margins / 2 + i * tabWidth, screen.y, true));
        }

        return list;
    }
}
