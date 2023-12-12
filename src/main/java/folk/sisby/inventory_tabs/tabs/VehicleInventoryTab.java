package folk.sisby.inventory_tabs.tabs;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.Predicate;

public class VehicleInventoryTab extends EntityTab {
    public VehicleInventoryTab(Entity entity, Map<Identifier, Predicate<Entity>> preclusions) {
        super(90, entity, preclusions, false);
    }

    @Override
    public void open(ClientPlayerEntity player, ClientWorld world, ScreenHandler handler, ClientPlayerInteractionManager interactionManager) {
        player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.OPEN_INVENTORY));
    }
}
