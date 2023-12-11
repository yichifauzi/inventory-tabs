package folk.sisby.inventory_tabs.mixin;

import net.minecraft.screen.ShulkerBoxScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ShulkerBoxScreenHandler.class)
public abstract class MixinShulkerBoxScreenHandler {
    @ModifyArg(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/ShulkerBoxSlot;<init>(Lnet/minecraft/inventory/Inventory;III)V"), index = 3)
    public int raiseContainerSlotY(int original) {
        return original - 1;
    }
}
