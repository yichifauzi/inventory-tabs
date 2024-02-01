package folk.sisby.inventory_tabs;

import folk.sisby.inventory_tabs.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ControlHintToast implements Toast {
    protected Text title;
    protected Text keyHint;
    protected KeyBinding keyBinding;
    protected int titleWidth;
    protected int hintWidth;

    public ControlHintToast(Text title, KeyBinding keybinding)
    {
        this.title = title;
        this.keyBinding = keybinding;
        keyHint = Text.translatable("toast.inventory_tabs.disabled.key_hint", keyBinding.getBoundKeyLocalizedText().copy().formatted(Formatting.YELLOW)).formatted(Formatting.BLUE);
        titleWidth = MinecraftClient.getInstance().textRenderer.getWidth(title);
        hintWidth = MinecraftClient.getInstance().textRenderer.getWidth(keyHint);
    }


    @Override
    public Visibility draw(MatrixStack matrices, ToastManager manager, long elapsedTime) {
        DrawUtil.drawCrunched(manager, matrices, TEXTURE, 0, 0, getWidth(), getHeight(), 160, 32, 0, 0);
        manager.getClient().textRenderer.draw(matrices, title, (float) (getWidth() - titleWidth) /2, 7, 0xFFFFFF);
        manager.getClient().textRenderer.draw(matrices, keyHint, (float) (getWidth() - hintWidth) /2, 18, 0xFFFFFF);

        double time = 2000;

        return elapsedTime >= time ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public int getWidth() {
        return Math.max(titleWidth, hintWidth) + 24;
    }
}
