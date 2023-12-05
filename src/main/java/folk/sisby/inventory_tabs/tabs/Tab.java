package folk.sisby.inventory_tabs.tabs;

import folk.sisby.inventory_tabs.InventoryTabs;
import folk.sisby.inventory_tabs.TabManager;
import folk.sisby.inventory_tabs.util.WidgetPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public interface Tab {
    Identifier TABS_TEXTURE = InventoryTabs.id("textures/gui/tabs.png");

    /**
     * Opens the screen associated with the tab.
     * @return false if the process to open the screen isn't possible.
     */
    boolean open();

    /**
     * @return true if the tab should stop being displayed. Should be synced up with the provider that provides this tab.
     */
    boolean shouldBeRemoved(World world, boolean current);

    /**
     * @return the stack to render as an icon in the default rendering method.
     */
    ItemStack getTabIcon();

    /**
     * @return the text that's displayed when hovering over the tab.
     */
    Text getHoverText();

    /**
     * Called when the screen associated with the tab is closed.
     */
    default void onClose(HandledScreen<?> currentScreen) {}

    /**
     * @return the tab's left-priority when being displayed. The player's inventory is at 100.
     */
    default int getPriority() {
        return 0;
    }

    default void renderBackground(GuiGraphics graphics, WidgetPosition pos, int width, int height, boolean current) {
        int y = pos.y + (pos.up ? -height + 4 : height - 4);
        if (!current) graphics.drawTexture(TABS_TEXTURE, pos.x, y, TabManager.TAB_WIDTH, pos.up ? 0 : (TabManager.TAB_HEIGHT * 2), width, height);
    }

    default void renderForeground(GuiGraphics graphics, WidgetPosition pos, int width, int height, double mouseX, double mouseY, boolean current) {
        int y = pos.y + (pos.up ? -height + 4 : height - 4);
        if (current) graphics.drawTexture(TABS_TEXTURE, pos.x, y, TabManager.TAB_WIDTH, TabManager.TAB_HEIGHT + (pos.up ? 0 : (TabManager.TAB_HEIGHT * 2)), width, height);
        int itemPadding = Math.max(0, (width - 16) / 2);
        graphics.drawItem(getTabIcon(), pos.x + itemPadding, y + itemPadding);
        if (new Rect2i(pos.x + itemPadding, y + itemPadding, 16, 16).contains((int) mouseX, (int) mouseY)) {
            graphics.drawTooltip(MinecraftClient.getInstance().textRenderer, getHoverText(), (int) mouseX, (int) mouseY);
        }
    }
}
