package folk.sisby.inventory_tabs.tabs;

import folk.sisby.inventory_tabs.util.HandlerSlotUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
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
    public int slot;
    public final boolean unique;
    public final boolean sneakInteract;
    public final Map<Identifier, Predicate<ItemStack>> preclusions;
    public ItemStack swappedStack = null;
    public int swappedSlot = -1;

    public ItemTab(ItemStack stack, int slot, Map<Identifier, Predicate<ItemStack>> preclusions, boolean unique, boolean sneakInteract) {
        this.stack = stack;
        this.slot = slot;
        this.preclusions = preclusions;
        this.unique = unique;
        this.sneakInteract = sneakInteract;
    }

    public ItemTab(ItemStack stack, int slot, Map<Identifier, Predicate<ItemStack>> preclusions, boolean unique) {
        this(stack, slot, preclusions, unique, false);
    }

    @Override
    public void close(ClientPlayerEntity player, ClientWorld world, ScreenHandler handler, ClientPlayerInteractionManager interactionManager) {
        if (player == null) return;
        if (swappedSlot != -1) {
            ItemStack inSwappedSlot = player.getInventory().getStack(swappedSlot);
            if (ItemStack.areEqual(inSwappedSlot, swappedStack)) {
                int slotIndex = handler.getSlotIndex(player.getInventory(), swappedSlot).getAsInt();
                interactionManager.clickSlot(handler.syncId, slotIndex, player.getInventory().selectedSlot, SlotActionType.SWAP, player);
            }
        }
    }

    @Override
    public void open(ClientPlayerEntity player, ClientWorld world, ScreenHandler handler, ClientPlayerInteractionManager interactionManager) {
        int slotIndex = handler.getSlotIndex(player.getInventory(), slot).getAsInt();
        if (slotIndex != player.getInventory().selectedSlot) interactionManager.clickSlot(handler.syncId, slotIndex, player.getInventory().selectedSlot, SlotActionType.SWAP, player);
        if (sneakInteract) player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        interactionManager.interactItem(player, Hand.MAIN_HAND);
        if (sneakInteract) player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        if (unique && slotIndex != player.getInventory().selectedSlot) HandlerSlotUtil.mainHandSwapSlot = slot; // Can't swap back for non-uniques
        if (!unique) {
            this.swappedSlot = this.slot;
            this.swappedStack = player.getInventory().getStack(this.slot);
            this.slot = player.getInventory().selectedSlot;
        }
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
    public boolean isBuffered() {
        return true;
    }

    @Override
    public Text getHoverText() {
        return !stack.getName().equals(stack.getItem().getName(stack)) ? stack.getName().copy().formatted(Formatting.ITALIC) : stack.getName();
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
