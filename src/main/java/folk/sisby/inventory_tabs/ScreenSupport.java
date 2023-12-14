package folk.sisby.inventory_tabs;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public class ScreenSupport {
    public static Map<Identifier, Predicate<HandledScreen<?>>> DENY = new HashMap<>();
    public static Map<Identifier, Predicate<HandledScreen<?>>> ALLOW = new HashMap<>();

    public static boolean allowTabs(Screen screen) {
        if (screen instanceof HandledScreen<?> hs && hs.getScreenHandler() != null) {
            if (DENY.values().stream().anyMatch(p -> p.test(hs))) return false;
            if (ALLOW.values().stream().anyMatch(p -> p.test(hs))) return true;
            try {
                Identifier handlerId = Registry.SCREEN_HANDLER.getId(hs.getScreenHandler().getType());
                if (InventoryTabs.CONFIG.screenOverrides.entrySet().stream().filter(e -> !e.getValue()).anyMatch(e -> Objects.equals(e.getKey(), handlerId.toString()))) return false;
                if (InventoryTabs.CONFIG.screenOverrides.entrySet().stream().filter(Map.Entry::getValue).anyMatch(e -> Objects.equals(e.getKey(), handlerId.toString()))) return true;
            } catch (UnsupportedOperationException ignored) {
            }
            return InventoryTabs.CONFIG.allowScreensByDefault;
        }
        return false;
    }

    static {
        DENY.put(InventoryTabs.id("creative_screen"), hs -> hs instanceof CreativeInventoryScreen);
        ALLOW.put(InventoryTabs.id("horse_screen"), hs -> hs instanceof HorseScreen);
    }
}
