package folk.sisby.inventory_tabs.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class DrawUtil {
    public static void drawCrunched(DrawableHelper helper, MatrixStack matrices, Identifier texture, int x, int y, int width, int height, int sourceWidth, int sourceHeight, int u, int v) {
        RenderSystem.setShaderTexture(0, texture);
        int leftWidth = width / 2 + (width % 2);
        int topHeight = height / 2 + (height % 2);
        int rightWidth = width - leftWidth;
        int bottomHeight = height - topHeight;

        helper.drawTexture(matrices, x, y, u, v, leftWidth, topHeight); // Top Left
        helper.drawTexture(matrices, x + leftWidth, y, u + sourceWidth - rightWidth, v, rightWidth, topHeight); // Top Right
        helper.drawTexture(matrices, x, y + topHeight, u, v + sourceHeight - bottomHeight, leftWidth, bottomHeight); // Bottom Left
        helper.drawTexture(matrices, x + leftWidth, y + topHeight, u + sourceWidth - rightWidth, v + sourceHeight - bottomHeight, rightWidth, bottomHeight); // Bottom Right
    }
}
