package folk.sisby.inventory_tabs.providers;

import folk.sisby.inventory_tabs.InventoryTabs;
import net.minecraft.block.BarrelBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SimpleStorageBlockTabProvider extends BlockTabProvider {
    public SimpleStorageBlockTabProvider() {
        super();
        matches.put(InventoryTabs.id("barrel_block"), b -> b instanceof BarrelBlock);
    }

    @Override
    public int getTabOrderPriority(World world, BlockPos pos) {
        return -50;
    }

    @Override
    public int getRegistryPriority() {
        return 10;
    }
}
