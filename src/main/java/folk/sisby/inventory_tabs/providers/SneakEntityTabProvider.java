package folk.sisby.inventory_tabs.providers;

import folk.sisby.inventory_tabs.InventoryTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.RideableOpenableInventory;
import net.minecraft.entity.passive.HorseBaseEntity;

public class SneakEntityTabProvider extends EntityTabProvider {
    public SneakEntityTabProvider () {
        super();
        warmMatches.put(InventoryTabs.id("rideable_openable_inventory"), e -> e instanceof RideableOpenableInventory);
        preclusions.put(InventoryTabs.id("untamed"), e -> e instanceof HorseBaseEntity h && !h.isTame());
    }

    @Override
    public int getRegistryPriority() {
        return 30;
    }

    @Override
    public int getTabOrderPriority(Entity entity) {
        return entity instanceof RideableOpenableInventory ? 45 : 40;
    }

    @Override
    public boolean doSneakInteract() {
        return true;
    }
}
