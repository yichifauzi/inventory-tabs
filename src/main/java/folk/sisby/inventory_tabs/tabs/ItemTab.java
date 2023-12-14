package folk.sisby.inventory_tabs.tabs;

import folk.sisby.inventory_tabs.util.HandlerSlotUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
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
    public void open(ClientPlayerEntity player, ClientWorld world, ScreenHandler handler, ClientPlayerInteractionManager interactionManager) {
        int slotIndex = handler.getSlotIndex(player.getInventory(), slot).getAsInt();
        interactionManager.clickSlot(handler.syncId, slotIndex, player.getInventory().selectedSlot, SlotActionType.SWAP, player);
        interactionManager.interactItem(player, player.world, Hand.MAIN_HAND);
        HandlerSlotUtil.mainHandSwapSlot = slot;
    }

    @Override
    public boolean shouldBeRemoved(World world, boolean current) {
        if (current) return false;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return true;
        if (!player.getInventory().getStack(slot).equals(stack)) return true;
        if (preclusions.values().stream().anyMatch(p -> p.test(stack))) return true;
        if (MinecraftClient.getInstance().currentScreen instanceof HandledScreen<?> hs && hs.getScreenHandler().getSlotIndex(player.getInventory(), slot).isEmpty()) return true;
        return false;
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
