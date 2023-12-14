package folk.sisby.inventory_tabs.providers;

import folk.sisby.inventory_tabs.InventoryTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseBaseEntity;

public class SneakEntityTabProvider extends EntityTabProvider {
    public SneakEntityTabProvider () {
        super();
        warmMatches.put(InventoryTabs.id("horse_base_entity"), e -> e instanceof HorseBaseEntity);
        preclusions.put(InventoryTabs.id("untamed"), e -> e instanceof HorseBaseEntity h && !h.isTame());
    }

    @Override
    public int getRegistryPriority() {
        return 30;
    }

    @Override
    public int getTabOrderPriority(Entity entity) {
        return entity instanceof HorseBaseEntity ? 45 : 40;
    }

    @Override
    public boolean doSneakInteract() {
        return true;
    }
}
