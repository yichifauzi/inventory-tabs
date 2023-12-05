package folk.sisby.inventory_tabs.mixin;

import folk.sisby.inventory_tabs.TabManager;
import folk.sisby.inventory_tabs.duck.InventoryTabsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class MixinScreen {
    @Inject(method = "renderBackground(Lnet/minecraft/client/util/math/MatrixStack;I)V", at = @At("TAIL"))
    void renderTabBackground(MatrixStack matrices, int vOffset, CallbackInfo ci) {
        if (!(this instanceof InventoryTabsScreen its) || !its.inventoryTabs$allowTabs()) return;
        TabManager.renderBackground(matrices);
    }
}
