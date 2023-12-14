package folk.sisby.inventory_tabs.providers;

import net.minecraft.entity.Entity;

public class SimpleEntityTabProvider extends EntityTabProvider {
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
