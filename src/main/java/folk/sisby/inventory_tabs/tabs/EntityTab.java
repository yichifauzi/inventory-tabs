package folk.sisby.inventory_tabs.tabs;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public class EntityTab implements Tab {
    public final int priority;
    public final Entity entity;
    public final boolean sneakInteract;
    public final Map<Identifier, Predicate<Entity>> preclusions;
    public ItemStack itemStack;

    public EntityTab(int priority, Entity entity, Map<Identifier, Predicate<Entity>> preclusions, boolean sneakInteract) {
        this.priority = priority;
        this.entity = entity;
        this.preclusions = preclusions;
        this.sneakInteract = sneakInteract;
        this.itemStack = entity.getPickBlockStack() != null ? entity.getPickBlockStack() : Items.BARRIER.getDefaultStack();
        refreshPreviewStack();
    }

    @Override
    public void open(ClientPlayerEntity player, ClientWorld world, ScreenHandler handler, ClientPlayerInteractionManager interactionManager) {
        player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.interact(entity, sneakInteract, player.getActiveHand()));
        if (sneakInteract) player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
    }

    @Override
    public boolean shouldBeRemoved(World world, boolean current) {
        if (current) return false;
        return preclusions.values().stream().anyMatch(p -> p.test(entity));
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public Text getHoverText() {
        return entity.hasCustomName() ? entity.getCustomName().copy().formatted(Formatting.ITALIC) : entity.getName();
    }

    @Override
    public ItemStack getTabIcon() {
        return itemStack;
    }

    protected void refreshPreviewStack() {
    }

    @Override
    public boolean equals(Object other) {
        return other != null && getClass() == other.getClass() && Objects.equals(entity.getUuid(), ((EntityTab) other).entity.getUuid());
    }
}
