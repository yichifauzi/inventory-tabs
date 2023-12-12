package folk.sisby.inventory_tabs.tabs;

import folk.sisby.inventory_tabs.util.HandlerSlotUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Predicate;

public class ItemTab implements Tab {
    public final ItemStack stack;
    public final int slot;
    public final boolean unique;
    public final Map<Identifier, Predicate<ItemStack>> preclusions;

    public ItemTab(ItemStack stack, int slot, Map<Identifier, Predicate<ItemStack>> preclusions, boolean unique) {
        this.stack = stack;
        this.slot = slot;
        this.preclusions = preclusions;
        this.unique = unique;
    }

    @Override
    public boolean open() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (player == null || !player.getInventory().getStack(slot).equals(stack) || preclusions.values().stream().noneMatch(p -> p.test(stack))) return false;
        if (screen instanceof HandledScreen<?> hs) {
            ScreenHandler handler = hs.getScreenHandler();
            OptionalInt slotIndex = handler.getSlotIndex(player.getInventory(), slot);
            if (slotIndex.isPresent()) {
                MinecraftClient.getInstance().interactionManager.clickSlot(handler.syncId, slotIndex.getAsInt(), player.getInventory().selectedSlot, SlotActionType.SWAP, player);
                MinecraftClient.getInstance().interactionManager.interactItem(player, Hand.MAIN_HAND);
                HandlerSlotUtil.mainHandSwapSlot = slot;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldBeRemoved(World world, boolean current) {
        if (current) return false;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        return player == null || !player.getInventory().getStack(slot).equals(stack) || preclusions.values().stream().noneMatch(p -> p.test(stack));
    }

    @Override
    public Text getHoverText() {
        return stack.hasCustomName() ? stack.getName().copy().formatted(Formatting.ITALIC) : stack.getName();
    }

    @Override
    public ItemStack getTabIcon() {
        return stack;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (unique) {
            return other instanceof ItemTab it && Objects.equals(stack.getItem(), it.stack.getItem()) ||
                    other instanceof BlockTab bt && Objects.equals(stack.getItem(), bt.block.asItem());
        } else {
            return other instanceof ItemTab it && Objects.equals(slot, it.slot);
        }
    }
}
