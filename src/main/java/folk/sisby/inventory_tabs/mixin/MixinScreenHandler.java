package folk.sisby.inventory_tabs.mixin;

import folk.sisby.inventory_tabs.TabManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ScreenHandler.class)
public abstract class MixinScreenHandler  {
    @Inject(method = "updateSlotStacks", at = @At("TAIL"))
    public void finishChangingTabs(int revision, List<ItemStack> stacks, ItemStack cursorStack, CallbackInfo ci) {
        if (revision == 1 && MinecraftClient.getInstance().player != null) {
            TabManager.finishOpeningScreen((ScreenHandler) (Object) this);
        }
    }
}
