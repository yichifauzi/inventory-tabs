package folk.sisby.inventory_tabs.providers;

import folk.sisby.inventory_tabs.InventoryTabs;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnderChestTabProvider extends BlockTabProvider {
    public EnderChestTabProvider() {
        super();
        matches.put(InventoryTabs.id("ender_chest_block"), b -> b instanceof EnderChestBlock);
        preclusions.put(InventoryTabs.id("chest_blocked"), ChestBlock::isChestBlocked);
    }

    @Override
    public int getTabOrderPriority(World world, BlockPos pos) {
        return -5;
    }

    @Override
    public int getRegistryPriority() {
        return 60;
    }
}
