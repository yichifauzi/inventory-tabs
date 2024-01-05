package folk.sisby.inventory_tabs.tabs;

import folk.sisby.inventory_tabs.util.WidgetPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public interface Tab {
    Identifier TABS_TEXTURE = new Identifier("textures/gui/container/creative_inventory/tabs.png");
    int TAB_TEXTURE_WIDTH = 26;
    int TAB_TEXTURE_HEIGHT = 32;
    int TAB_TEXTURE_U = 26;
    int TAB_TEXTURE_V_UNSELECTED = 2;
    int TAB_TEXTURE_V_SELECTED = 32;
    int TAB_TEXTURE_V_INVERTED_OFFSET = 64;

    /**
     * Opens the screen associated with the tab.
     */
    void open(ClientPlayerEntity player, ClientWorld world, ScreenHandler handler, ClientPlayerInteractionManager interactionManager);

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
     * Called when the screen associated with the tab is closed (for handlers that aren't destroyed when closed on the servers)
     */
    default void close() {}

    /**
     * @return the tab's left-priority when being displayed. The player's inventory is at 100.
     */
    default int getPriority() {
        return 0;
    }

    /**
     * @return whether the tabs open method instantly opens the screen on the client side without slot sync.
     * Used for the survival inventory. Helps preserve cursor stacks.
     */
    default boolean isInstant() { return false; }

    default void renderBackground(DrawContext drawContext, WidgetPosition pos, int width, int height, boolean current) {
        int y = pos.y + (pos.up ? -height + 4 : height - 4);
        if (!current) drawContext.drawNineSlicedTexture(TABS_TEXTURE, pos.x, y, width, height, 6, TAB_TEXTURE_WIDTH, TAB_TEXTURE_HEIGHT, TAB_TEXTURE_U, TAB_TEXTURE_V_UNSELECTED + (pos.up ? 0 : TAB_TEXTURE_V_INVERTED_OFFSET));
    }

    default void renderForeground(DrawContext drawContext, WidgetPosition pos, int width, int height, double mouseX, double mouseY, boolean current) {
        int y = pos.y + (pos.up ? -height + 4 : height - 4);
        if (current) drawContext.drawNineSlicedTexture(TABS_TEXTURE, pos.x, y, width, height, 6, TAB_TEXTURE_WIDTH, TAB_TEXTURE_HEIGHT, TAB_TEXTURE_U, TAB_TEXTURE_V_SELECTED + (pos.up ? 0 : TAB_TEXTURE_V_INVERTED_OFFSET));
        int itemPadding = Math.max(0, (width - 16) / 2);
        drawContext.drawItem(getTabIcon(), pos.x + itemPadding, y + itemPadding);
        if (new Rect2i(pos.x + itemPadding, y + itemPadding, 16, 16).contains((int) mouseX, (int) mouseY)) {
            drawContext.drawTooltip(MinecraftClient.getInstance().textRenderer, getHoverText(), (int) mouseX, (int) mouseY);
        }
    }
}
