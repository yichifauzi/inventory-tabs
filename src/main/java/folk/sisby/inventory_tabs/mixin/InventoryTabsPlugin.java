package folk.sisby.inventory_tabs.mixin;

import folk.sisby.inventory_tabs.InventoryTabs;
import net.fabricmc.loader.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

@SuppressWarnings("deprecation")
public class InventoryTabsPlugin implements IMixinConfigPlugin {
    public static final List<String> FORGE_BANNED_MIXINS = List.of(
            "folk.sisby.inventory_tabs.mixin.MixinKeyBinding"
    );

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith("folk.sisby.inventory_tabs.mixin.")) {
            if (FabricLoader.INSTANCE.isModLoaded("connectormod") && FORGE_BANNED_MIXINS.contains(mixinClassName)) {
                return false;
            }
            if (!InventoryTabs.CONFIG.consistentContainers && (mixinClassName.contains("GenericContainer") || mixinClassName.contains("ShulkerBox"))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}