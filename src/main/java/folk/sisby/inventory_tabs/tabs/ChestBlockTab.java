package folk.sisby.inventory_tabs.tabs;

import folk.sisby.inventory_tabs.util.ChestUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.function.BiPredicate;

import static folk.sisby.inventory_tabs.util.ChestUtil.isDouble;

public class ChestBlockTab extends BlockTab {
    public ChestBlockTab(World world, BlockPos pos, Map<Identifier, BiPredicate<World, BlockPos>> preclusions, int priority) {
        super(world, pos, preclusions, priority, false);
    }

    @Override
    protected Text getDefaultHoverText(World world) {
        return Text.translatable(isDouble(world, pos) ? "container.chestDouble" : "container.chest");
    }

    @Override
    protected void refreshMultiblock(World world) {
        multiblockPositions.clear();
        multiblockPositions.addAll(ChestUtil.getChestMultiblockPos(world, pos));
    }
}
