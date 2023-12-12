package folk.sisby.inventory_tabs.tabs;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.Predicate;

public class VehicleInventoryTab extends EntityTab {
    public VehicleInventoryTab(Entity entity, Map<Identifier, Predicate<Entity>> preclusions) {
        super(90, entity, preclusions, false);
    }

    @Override
    public boolean open() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || preclusions.values().stream().anyMatch(p -> p.test(entity))) return false;
        player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.OPEN_INVENTORY));
        return true;
    }
}
