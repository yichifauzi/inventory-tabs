package folk.sisby.inventory_tabs.tabs;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;

public class PlayerInventoryTab implements Tab {
    public static final Text TITLE = new TranslatableText("gui.inventory_tabs.tab.inventory");
    public ItemStack itemStack;

    public PlayerInventoryTab() {
        itemStack = new ItemStack(Blocks.PLAYER_HEAD);
        itemStack.getOrCreateNbt().putString("SkullOwner", MinecraftClient.getInstance().player.getGameProfile().getName());
    }

    @Override
    public void open(ClientPlayerEntity player, ClientWorld world, ScreenHandler handler, ClientPlayerInteractionManager interactionManager) {
        MinecraftClient.getInstance().setScreen(new InventoryScreen(player));
    }

    @Override
    public void close(ClientPlayerEntity player, ClientWorld world, ScreenHandler handler, ClientPlayerInteractionManager interactionManager) {
        if (player != null) player.playerScreenHandler.setCursorStack(ItemStack.EMPTY);
    }

    @Override
    public boolean shouldBeRemoved(World world, boolean current) {
        return false;
    }

    @Override
    public Text getHoverText() {
        return TITLE;
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public ItemStack getTabIcon() {
        return itemStack;
    }

    @Override
    public boolean equals(Object other) {
        return other != null && getClass() == other.getClass();
    }
}
