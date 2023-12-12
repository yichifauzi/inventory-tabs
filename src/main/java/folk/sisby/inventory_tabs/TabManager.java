package folk.sisby.inventory_tabs;

import folk.sisby.inventory_tabs.duck.InventoryTabsScreen;
import folk.sisby.inventory_tabs.tabs.BlockTab;
import folk.sisby.inventory_tabs.tabs.EntityTab;
import folk.sisby.inventory_tabs.tabs.ItemTab;
import folk.sisby.inventory_tabs.tabs.Tab;
import folk.sisby.inventory_tabs.tabs.VehicleInventoryTab;
import folk.sisby.inventory_tabs.util.HandlerSlotUtil;
import folk.sisby.inventory_tabs.util.WidgetPosition;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.packet.c2s.play.HandledScreenCloseC2SPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class TabManager {
    public static final Identifier BUTTONS_TEXTURE = InventoryTabs.id("textures/gui/buttons.png");
    public static final int TAB_WIDTH = 24;
    public static final int TAB_HEIGHT = 25;
    public static final int BUTTON_WIDTH = 10;
    public static final int BUTTON_HEIGHT = 18;

    public static final Map<Identifier, BiFunction<HandledScreen<?>, List<Tab>, Tab>> tabGuessers = new HashMap<>();

    public static boolean changingTabs;
    public static HandledScreen<?> currentScreen;
    public static final List<Tab> tabs = new ArrayList<>();
    public static int currentPage = 0;
    public static Tab currentTab;
    public static List<WidgetPosition> tabPositions;
    public static boolean skipRestore;

    public static void initScreen(MinecraftClient client, HandledScreen<?> screen) {
        currentScreen = screen;
        tabPositions = ((InventoryTabsScreen) currentScreen).getTabPositions(TAB_WIDTH);
        if (!changingTabs) onOpenTab(guessOpenedTab(client, screen));
        changingTabs = false;
        if (!skipRestore) {
            HandlerSlotUtil.tryPop(MinecraftClient.getInstance().player, MinecraftClient.getInstance().interactionManager, currentScreen.getScreenHandler());
        } else {
            skipRestore = false;
        }
    }

    public static Tab guessOpenedTab(MinecraftClient client, HandledScreen<?> screen) {
        World world = client.player.getWorld();
        // "Open Inventory" Guesses
        if (currentScreen instanceof InventoryScreen) return tabs.get(0);
        if (client.player.hasVehicle()) {
            for (Tab tab : tabs) {
                if (tab instanceof VehicleInventoryTab vit) {
                    if (client.player.getVehicle().equals(vit.entity)) {
                        return tab;
                    }
                }
            }
        }
        for (BiFunction<HandledScreen<?>, List<Tab>, Tab> guesser : tabGuessers.values()) {
            Tab guessedTab = guesser.apply(screen, tabs);
            if (guessedTab != null) return guessedTab;
        }
        // Crosshair Guesses
        if (client.crosshairTarget instanceof BlockHitResult result) {
            BlockPos pos = result.getBlockPos();
            BlockEntity blockEntity = world.getBlockEntity(pos);
            for (Tab tab : tabs) {
                if (tab instanceof BlockTab bt) {
                    if (pos.equals(bt.pos) || blockEntity == world.getBlockEntity(bt.pos)) return tab;
                }
            }
        } else if (client.crosshairTarget instanceof EntityHitResult result) {
            Entity entity = result.getEntity();
            for (Tab tab : tabs) {
                if (tab instanceof EntityTab et) {
                    if (entity.equals(et.entity)) {
                        return tab;
                    }
                }
            }
        }
        // Hand Guesses
        for (int slot : List.of(client.player.getInventory().selectedSlot, PlayerInventory.OFF_HAND_SLOT)) {
            for (Tab tab : tabs) {
                if (tab instanceof ItemTab it) {
                    if (slot == it.slot) {
                        return tab;
                    }
                }
            }
        }
        return null;
    }

    public static void tick(World world) {
        if (tabs.removeIf(t -> t.shouldBeRemoved(world, t == currentTab))) {
            sortTabs();
        }
        TabProviders.REGISTRY.values().forEach(tabProvider -> tabProvider.addAvailableTabs(MinecraftClient.getInstance().player, TabManager::tryAddTab));
        if (currentTab != null && !tabs.contains(currentTab)) currentTab = null;
    }

    public static void tryAddTab(Tab tab) {
        if (!tabs.contains(tab)) {
            tabs.add(tab);
            sortTabs();
        }
    }

    public static void sortTabs() {
        tabs.sort(Comparator.comparingInt(Tab::getPriority).reversed().thenComparing(t -> t.getHoverText().getString()));
    }

    public static void clearTabs() {
        tabs.clear();
    }

    public static boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!changingTabs && button == 0) {
            if (getPageButton(true).contains((int) mouseX, (int) mouseY)) {
                if (currentPage > 0) {
                    setCurrentPage(currentPage - 1);
                    playClick();
                }
                return true;
            }

            if (getPageButton(false).contains((int) mouseX, (int) mouseY)) {
                if (currentPage < getMaximumPage()) {
                    setCurrentPage(currentPage + 1);
                    playClick();
                }
                return true;
            }

            for (int i = 0; i < Math.min(tabPositions.size(), tabs.size() - currentPage * tabPositions.size()); i++) {
                WidgetPosition pos = tabPositions.get(i);
                Tab tab = tabs.get(currentPage * tabPositions.size() + i);
                if (pos != null && tab != null && tab != currentTab) {
                    if (getTabArea(pos).contains((int) mouseX, (int) mouseY)) {
                        onTabClick(tab);
                        playClick();
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean isClickOutsideBounds(double mouseX, double mouseY) {
        return !getPageButton(true).contains((int) mouseX, (int) mouseY) && !getPageButton(false).contains((int) mouseX, (int) mouseY) && tabPositions.stream().noneMatch(pos -> getTabArea(pos).contains((int) mouseX, (int) mouseY));
    }

    public static boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!changingTabs && InventoryTabs.NEXT_TAB.matchesKey(keyCode, scanCode)) {
            if (Screen.hasShiftDown()) {
                if (tabs.indexOf(currentTab) == 0) {
                    onTabClick(tabs.get(tabs.size() - 1));
                } else {
                    onTabClick(tabs.get(tabs.indexOf(currentTab) - 1));
                }
            } else {
                if (tabs.indexOf(currentTab) == tabs.size() - 1) {
                    onTabClick(tabs.get(0));
                } else {
                    onTabClick(tabs.get(tabs.indexOf(currentTab) + 1));
                }
            }
            return true;
        }

        return false;
    }

    public static void onTabClick(Tab tab) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        ClientPlayerInteractionManager interactionManager = MinecraftClient.getInstance().interactionManager;
        ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if (player != null && interactionManager != null && networkHandler != null && player.getWorld() instanceof ClientWorld world) {
            if (!tab.shouldBeRemoved(world, false)) {
                skipRestore = true;
                changingTabs = true;
                HandlerSlotUtil.push(player, MinecraftClient.getInstance().interactionManager, currentScreen.getScreenHandler());
                player.networkHandler.sendPacket(new HandledScreenCloseC2SPacket(currentScreen.getScreenHandler().syncId));
                tab.open(player, world, currentScreen.getScreenHandler(), interactionManager);
                onOpenTab(tab);
                if (!skipRestore) {
                    HandlerSlotUtil.tryPop(player, MinecraftClient.getInstance().interactionManager, currentScreen.getScreenHandler());
                }
            }
            skipRestore = false;
        }
    }

    public static void onOpenTab(Tab tab) {
        if (currentTab != null && currentTab != tab) currentTab.onClose(currentScreen);
        currentTab = tab;
        setCurrentPage(tabs.indexOf(tab) / tabPositions.size());
    }

    public static void setCurrentPage(int page) {
        if (page == 0 || tabs.size() >= tabPositions.size()) currentPage = page;
    }

    public static int getMaximumPage() {
        return tabs.size() / (tabPositions.size() + 1);
    }

    public static void renderBackground(GuiGraphics graphics) {
        tabPositions = ((InventoryTabsScreen) currentScreen).getTabPositions(TAB_WIDTH);
        for (int i = 0; i < Math.min(tabPositions.size(), tabs.size() - currentPage * tabPositions.size()); i++) {
            WidgetPosition pos = tabPositions.get(i);
            Tab tab = tabs.get(currentPage * tabPositions.size() + i);
            if (pos != null && tab != null) tab.renderBackground(graphics, pos, TAB_WIDTH, TAB_HEIGHT, tab == currentTab);
        }
    }

    public static void renderForeground(GuiGraphics graphics, double mouseX, double mouseY) {
        for (int i = 0; i < Math.min(tabPositions.size(), tabs.size() - currentPage * tabPositions.size()); i++) {
            WidgetPosition pos = tabPositions.get(i);
            Tab tab = tabs.get(currentPage * tabPositions.size() + i);
            if (pos != null && tab != null) tab.renderForeground(graphics, pos, TAB_WIDTH, TAB_HEIGHT, mouseX, mouseY,tab == currentTab);
        }

        if (getMaximumPage() > 0) {
            drawButton(graphics, mouseX, mouseY, true);
            drawButton(graphics, mouseX, mouseY, false);
        }
    }

    public static Rect2i getPageButton(boolean left) {
        WidgetPosition pos = tabPositions.get(left ? 0 : tabPositions.size() - 1);
        return new Rect2i(pos.x + (left ? -BUTTON_WIDTH : TAB_WIDTH), pos.y - BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT);
    }

    public static Rect2i getTabArea(WidgetPosition pos) {
        return new Rect2i(pos.x, pos.y + (pos.up ? -TAB_HEIGHT : TAB_HEIGHT), TAB_WIDTH, TAB_HEIGHT);
    }

    public static void drawButton(GuiGraphics graphics, double mouseX, double mouseY, boolean left) {
        Rect2i rect = getPageButton(left);
        boolean hovered = rect.contains((int) mouseX, (int) mouseY);
        boolean active = left ? currentPage > 0 : currentPage < getMaximumPage();
        int u = BUTTON_WIDTH * (left ? 0 : 1);
        int v = BUTTON_HEIGHT * (active ? hovered ? 2 : 1 : 0);
        graphics.drawTexture(BUTTONS_TEXTURE, rect.getX(), rect.getY(), u, v, rect.getWidth(), rect.getHeight());
        if (hovered) graphics.drawTooltip(currentScreen.getTextRenderer(), Text.literal((currentPage + 1) + "/" + (getMaximumPage() + 1)), (int) mouseX, (int) mouseY);
    }

    public static void playClick() {
        MinecraftClient.getInstance().getSoundManager()
                .play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F));
    }

}


