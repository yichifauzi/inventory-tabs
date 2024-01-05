package folk.sisby.inventory_tabs.util;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class DrawUtil {
    public static void drawCrunched(DrawContext context, Identifier texture, int x, int y, int width, int height, int sourceWidth, int sourceHeight, int u, int v) {
        int leftWidth = width / 2 + (width % 2);
        int topHeight = height / 2 + (height % 2);
        int rightWidth = width - leftWidth;
        int bottomHeight = height - topHeight;

        context.drawTexture(texture, x, y, u, v, leftWidth, topHeight); // Top Left
        context.drawTexture(texture, x + leftWidth, y, u + sourceWidth - rightWidth, v, rightWidth, topHeight); // Top Right
        context.drawTexture(texture, x, y + topHeight, u, v + sourceHeight - bottomHeight, leftWidth, bottomHeight); // Bottom Left
        context.drawTexture(texture, x + leftWidth, y + topHeight, u + sourceWidth - rightWidth, v + sourceHeight - bottomHeight, rightWidth, bottomHeight); // Bottom Right
    }
}
