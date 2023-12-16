package folk.sisby.inventory_tabs.providers;

import folk.sisby.inventory_tabs.InventoryTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;

public class SimpleEntityTabProvider extends EntityTabProvider {
    public SimpleEntityTabProvider() {
        warmMatches.put(InventoryTabs.id("storage_minecart_entity"), e -> e instanceof StorageMinecartEntity);
    }

    @Override
    public int getRegistryPriority() {
        return 20;
    }

    @Override
    public int getTabOrderPriority(Entity entity) {
        return 40;
    }

    @Override
    public boolean doSneakInteract() {
        return false;
    }
}
