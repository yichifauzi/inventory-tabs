package folk.sisby.inventory_tabs.mixin;

import java.util.Map;
import java.util.Objects;

import folk.sisby.inventory_tabs.InventoryTabs;
import folk.sisby.inventory_tabs.duck.InventoryTabsScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

@Mixin(KeyBinding.class)
public abstract class MixinKeyBind {
	@Shadow @Final private static Map<String, KeyBinding> KEY_BINDS_BY_KEY;
	
	@Shadow private InputUtil.Key boundKey;
	
	@Shadow private int timesPressed;
	
	@Inject(method = "onKeyPressed", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/KeyBind;timesPressed:I"),
			locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private static void onKeyPressed(InputUtil.Key key, CallbackInfo ci, KeyBinding binding) {
		MixinKeyBind alternative = (MixinKeyBind) (Object) findAlternative(key, binding, InventoryTabs.NEXT_TAB);
		if(alternative != null) {
			alternative.timesPressed++;
			ci.cancel();
		}
	}
	
	@Inject(method = "setKeyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBind;setPressed(Z)V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private static void keyPressed(InputUtil.Key key, boolean pressed, CallbackInfo ci, KeyBinding KeyBind) {
		KeyBinding alternative = findAlternative(key, KeyBind, InventoryTabs.NEXT_TAB);
		if(alternative != null) {
			alternative.setPressed(pressed);
			ci.cancel();
		}
	}
	
	@Unique private static KeyBinding findAlternative(InputUtil.Key key, KeyBinding binding, KeyBinding alternativeTo) {
		if(binding == alternativeTo && (!(MinecraftClient.getInstance().currentScreen instanceof InventoryTabsScreen its) || !its.inventoryTabs$allowTabs())) {
			for(KeyBinding value : KEY_BINDS_BY_KEY.values()) {
				MixinKeyBind self = (MixinKeyBind) (Object) value;
				InputUtil.Key bound = self.boundKey;
				if(Objects.equals(bound, key) && value != alternativeTo) {
					return value;
				}
			}
		}
		return null;
	}
}
