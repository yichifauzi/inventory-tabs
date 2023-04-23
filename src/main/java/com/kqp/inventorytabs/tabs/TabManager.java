package com.kqp.inventorytabs.tabs;

import com.kqp.inventorytabs.api.TabProviderRegistry;
import com.kqp.inventorytabs.init.InventoryTabsClient;
import com.kqp.inventorytabs.interf.TabManagerContainer;
import com.kqp.inventorytabs.mixin.accessor.HandledScreenAccessor;
import com.kqp.inventorytabs.tabs.render.TabRenderInfo;
import com.kqp.inventorytabs.tabs.render.TabRenderer;
import com.kqp.inventorytabs.tabs.render.TabRenderingHints;
import com.kqp.inventorytabs.tabs.tab.Tab;
import com.kqp.inventorytabs.util.MouseUtil;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.sound.SoundEvents;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Manages everything related to tabs.
 */
@Environment(EnvType.CLIENT)
public class TabManager {
    public final boolean isBigInvLoaded = FabricLoader.getInstance().isModLoaded("biginv");
    public final boolean isPlayerExLoaded = FabricLoader.getInstance().isModLoaded("playerex");
    public final List<Tab> tabs;
    public Tab currentTab;

    private HandledScreen<?> currentScreen;
    public int currentPage = 0;
    public boolean tabOpenedRecently;
    public int prevCursorStackSlot = -1;

    public final TabRenderer tabRenderer;

    public TabManager() {
        this.tabs = new ArrayList<>();
        this.tabRenderer = new TabRenderer(this);
    }

    public void update() {
        refreshAvailableTabs();

        tabRenderer.update();
    }

    public void setCurrentTab(Tab tab) {
        this.currentTab = tab;
    }

    private void refreshAvailableTabs() {
        // Remove old ones
        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i).shouldBeRemoved()) {
                tabs.remove(i);
                i--;
            }
        }

        // Add new tabs
        TabProviderRegistry.getTabProviders().forEach(tabProvider -> {
            tabProvider.addAvailableTabs(MinecraftClient.getInstance().player, tabs);
        });

        if (currentTab != null) {
            for (int i = 0; i < tabs.size(); i++) {
                Tab tab = tabs.get(i);
                if (currentTab != tab && currentTab.equals(tab)) {
                    // We've come across a tab we already have open
                    tabs.set(i, currentTab);
                    break;
                }
            }
        }

        // Sort
        tabs.sort(
                Comparator.comparing(Tab::getPriority).reversed().thenComparing(tab -> tab.getHoverText().getString()));
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int guiWidth = ((HandledScreenAccessor) currentScreen).getBackgroundWidth();
            int guiHeight = ((HandledScreenAccessor) currentScreen).getBackgroundHeight();
            int x = (currentScreen.width - guiWidth) / 2;
            int y = (currentScreen.height - guiHeight) / 2;

            if (mouseX > x && mouseX < x + guiWidth && mouseY > y && mouseY < y + guiHeight) {
                return false;
            }

            // Check back button
            if (new Rectangle(x - TabRenderer.BUTTON_WIDTH - 4 + ((TabRenderingHints) currentScreen).getTopRowXOffset(), y - 16, TabRenderer.BUTTON_WIDTH,
                    TabRenderer.BUTTON_HEIGHT).contains(mouseX, mouseY)) {
                if (canGoBackAPage()) {
                    setCurrentPage(currentPage - 1);
                    playClick();

                    return true;
                }
            }

            // Check forward button
            if (new Rectangle(x + guiWidth + 4 + ((TabRenderingHints) currentScreen).getTopRowXOffset(), y - 16, TabRenderer.BUTTON_WIDTH, TabRenderer.BUTTON_HEIGHT)
                    .contains(mouseX, mouseY)) {
                if (canGoForwardAPage()) {
                    setCurrentPage(currentPage + 1);
                    playClick();

                    return true;
                }
            }

            TabRenderInfo[] tabRenderInfos = tabRenderer.getTabRenderInfos();

            for (int i = 0; i < tabRenderInfos.length; i++) {
                TabRenderInfo tabRenderInfo = tabRenderInfos[i];

                if (tabRenderInfo != null) {
                    if (tabRenderInfo.tabReference != currentTab) {
                        Rectangle rect = new Rectangle(tabRenderInfo.x, tabRenderInfo.y, tabRenderInfo.texW,
                                tabRenderInfo.texH);

                        if (rect.contains(mouseX, mouseY)) {
                            onTabClick(tabRenderInfo.tabReference);

                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (InventoryTabsClient.NEXT_TAB_KEY_BIND.matchesKey(keyCode, scanCode)) {
            int currentTabIndex = tabs.indexOf(currentTab);
            if (Screen.hasShiftDown()) {
                if (currentTabIndex > 0) {
                    onTabClick(tabs.get(currentTabIndex - 1));
                } else {
                    onTabClick(tabs.get(tabs.size() - 1));
                }
                return true;
            } else {
                if (currentTabIndex < tabs.size() - 1) {
                    onTabClick(tabs.get(currentTabIndex + 1));
                } else {
                    onTabClick(tabs.get(0));
                }

                return true;
            }
        }

        return false;
    }

    public void onScreenOpen(HandledScreen<?> screen) {
        refreshAvailableTabs();

        setCurrentScreen(screen);
        MouseUtil.tryPop();
    }

    public void restoreCursorStack(ClientPlayerInteractionManager manager, ClientPlayerEntity player, ScreenHandler currentHandler) {
        // Try restore the cursor stack if it exists and wasn't dropped.
        if (manager!= null && this.prevCursorStackSlot != -1) {
            currentHandler.getSlotIndex(player.getInventory(), this.prevCursorStackSlot).ifPresent((screenSlot) ->{
                manager.clickSlot(
                        currentHandler.syncId,
                        screenSlot,
                        0, // Mouse Left Click
                        SlotActionType.PICKUP,
                        player
                );
            });
            this.prevCursorStackSlot = -1;
        }
    }

    public void onTabClick(Tab tab) {
        // Push current mouse position
        // This is to persist mouse position across screens
        MouseUtil.push();

        // Set tab open flag
        tabOpenedRecently = true;

        MinecraftClient client = MinecraftClient.getInstance();
        ScreenHandler handler = client.player.currentScreenHandler;
        this.prevCursorStackSlot = -1;

        if (handler != null) {

            // Preserve the cursor stack
            ItemStack prevCursorStack = client.player.currentScreenHandler.getCursorStack();
            if (prevCursorStack != null && !prevCursorStack.isEmpty()) {
                this.prevCursorStackSlot = client.player.getInventory().getEmptySlot();

                if (this.prevCursorStackSlot != 1 && client.interactionManager != null) {
                    // Put the cursor stack there
                    handler.getSlotIndex(client.player.getInventory(), this.prevCursorStackSlot).ifPresent((screenSlot) -> {
                        client.interactionManager.clickSlot(
                                handler.syncId,
                                screenSlot,
                                0, // Mouse Left Click
                                SlotActionType.PICKUP,
                                client.player
                        );
                    });
                }
            }

            // Close any handled screens
            // This fixes the inventory desync issue
            client.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(handler.syncId));
        }

        // Open new tab
        onOpenTab(tab);
        tab.open();
    }

    public void onOpenTab(Tab tab) {
        if (currentTab != null && currentTab != tab) {
            currentTab.onClose();
        }

        setCurrentTab(tab);
        setCurrentPage(pageOf(tab));
    }

    public int pageOf(Tab tab) {
        int index = tabs.indexOf(tab);
        if(isBigInvLoaded) {
            return index / (getMaxRowLength() * 2 + 5);
        } else if(isPlayerExLoaded) {
            //System.out.println("getMaxRowLength() = " + getMaxRowLength() + ", getMaxRowLength() * 2 - 3 = " + (getMaxRowLength() * 2 - 3));
            return index / (getMaxRowLength() * 2 - 2);
        } else {
            return index / (getMaxRowLength() * 2);
        }
    }

    public int getMaxRowLength() {
        int guiWidth = ((HandledScreenAccessor) currentScreen).getBackgroundWidth();
        int maxRowLength = guiWidth / (TabRenderer.TAB_WIDTH + 1);

        return maxRowLength;
    }

    public void setCurrentScreen(HandledScreen<?> screen) {
        this.currentScreen = screen;
    }

    public HandledScreen<?> getCurrentScreen() {
        return currentScreen;
    }

    public void setCurrentPage(int page) {
        int maxRowLength = getMaxRowLength() * 2;
        if (isPlayerExLoaded) {
            maxRowLength =- 3;
        }
        if (page > 0 && tabs.size() < maxRowLength) {
            System.err.println("Not enough tabs to paginate, ignoring");

            return;
        }

        if (this.currentPage != page) {
            tabRenderer.resetPageTextRefreshTime();
        }

        this.currentPage = page;
    }

    public boolean screenOpenedViaTab() {
        if (tabOpenedRecently) {
            tabOpenedRecently = false;

            return true;
        }

        return false;
    }

    public int getMaxPages() {
        if(isBigInvLoaded) {
            return tabs.size() / (getMaxRowLength() * 2 + 6);
        } else if(isPlayerExLoaded) {
            return tabs.size() / (getMaxRowLength() * 2 - 2);
        } else {
            return tabs.size() / (getMaxRowLength() * 2 + 1);
        }
    }

    public boolean canGoBackAPage() {
        return currentPage != 0;
    }

    public boolean canGoForwardAPage() {
        return currentPage < getMaxPages();
    }

    public static TabManager getInstance() {
        return ((TabManagerContainer) MinecraftClient.getInstance()).getTabManager();
    }

    public static void playClick() {
        MinecraftClient.getInstance().getSoundManager()
                .play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}

