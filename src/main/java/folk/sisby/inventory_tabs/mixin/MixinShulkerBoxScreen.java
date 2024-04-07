package folk.sisby.inventory_tabs.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShulkerBoxScreen.class)
public abstract class MixinShulkerBoxScreen extends HandledScreen<GenericContainerScreenHandler> {
    public MixinShulkerBoxScreen(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void containerTextHeight(ShulkerBoxScreenHandler handler, PlayerInventory inventory, Text title, CallbackInfo ci) {
        this.backgroundHeight -= 1;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Inject(method = "drawBackground", at = @At("TAIL"))
    public void containerHeader(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        drawTexture(matrices, (this.width - this.backgroundWidth) / 2, (this.height - this.backgroundHeight) / 2, 0, 0, this.backgroundWidth, 7);
    }

    @ModifyArg(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/ShulkerBoxScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 0), index = 2)
    public int containerY(int original) {
        return original + 7;
    }

    @ModifyArg(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/ShulkerBoxScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 0), index = 4)
    public int containerV(int original) {
        return original + 8;
    }

    @ModifyArg(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/ShulkerBoxScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 0), index = 6)
    public int containerHeight(int original) {
        return 64;
    }

    @Inject(method = "drawBackground", at = @At("TAIL"))
    public void containerInventory(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        drawTexture(matrices, (this.width - this.backgroundWidth) / 2, (this.height - this.backgroundHeight) / 2 + 71, 0, 71, this.backgroundWidth, 96);
    }
}
