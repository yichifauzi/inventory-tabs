package folk.sisby.inventory_tabs.providers;

import folk.sisby.inventory_tabs.InventoryTabs;
import folk.sisby.inventory_tabs.tabs.Tab;
import folk.sisby.inventory_tabs.tabs.VehicleInventoryTab;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.RideableOpenableInventory;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class VehicleInventoryTabProvider implements TabProvider {
    public final Map<Identifier, Predicate<Entity>> preclusions = new HashMap<>();

    public VehicleInventoryTabProvider() {
        preclusions.put(InventoryTabs.id("removed"), Entity::isRemoved);
        preclusions.put(InventoryTabs.id("vehicle"), e -> MinecraftClient.getInstance().player != null && e != MinecraftClient.getInstance().player.getVehicle());
    }

    @Override
    public void addAvailableTabs(ClientPlayerEntity player, Consumer<Tab> addTab) {
        if (player.hasVehicle() && player.getVehicle() instanceof RideableOpenableInventory) {
            addTab.accept(new VehicleInventoryTab(player.getVehicle(), preclusions));
        }
    }
}
