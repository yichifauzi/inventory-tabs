package com.kqp.inventorytabs.init;

import com.kqp.inventorytabs.api.TabProviderRegistry;
import com.kqp.inventorytabs.interf.TabManagerContainer;

import net.minecraft.client.gui.screen.ingame.*;
import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class InventoryTabsClient implements ClientModInitializer {
    public static final KeyBinding NEXT_TAB_KEY_BIND = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "inventorytabs.key.next_tab", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_TAB, "key.categories.inventory"));

    public static boolean serverDoSightCheckFlag = true;

    @Override
    public void onInitializeClient() {
        TabProviderRegistry.init();

        // Handle state of tab managerInventoryTabsClient
        ClientTickEvents.START_WORLD_TICK.register(world -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.currentScreen != null) {
                TabManagerContainer tabManagerContainer = (TabManagerContainer) client;

                tabManagerContainer.getTabManager().update();
            }
        });
    }
    
    public static boolean screenSupported(Screen screen) {
        return screen instanceof GenericContainerScreen || screen instanceof InventoryScreen
                || screen instanceof AbstractFurnaceScreen || screen instanceof AnvilScreen
                || screen instanceof CraftingScreen || screen instanceof ShulkerBoxScreen
                || screen instanceof EnchantmentScreen || screen instanceof BrewingStandScreen
                || screen instanceof SmithingScreen || screen instanceof CartographyTableScreen
                || screen instanceof LoomScreen || screen instanceof StonecutterScreen
                || screen instanceof GrindstoneScreen || screen instanceof HopperScreen
                || screen instanceof Generic3x3ContainerScreen;
    }
}
