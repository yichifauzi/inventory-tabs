package folk.sisby.inventory_tabs.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChestUtil {
    public static boolean isDouble(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.contains(Properties.CHEST_TYPE) && blockState.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE;
    }

    public static BlockPos getOtherChestBlockPos(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.get(ChestBlock.CHEST_TYPE) == ChestType.LEFT) return pos.offset(blockState.get(ChestBlock.FACING).rotateYClockwise());
        return pos.offset(blockState.get(ChestBlock.FACING).rotateYCounterclockwise());
    }

    public static List<BlockPos> getChestMultiblockPos(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (!blockState.contains(Properties.CHEST_TYPE) || blockState.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE) return List.of(pos);
        List<BlockPos> list = new ArrayList<>();
        list.add(pos);
        list.add(getOtherChestBlockPos(world, pos));
        if (blockState.contains(Properties.CHEST_TYPE) && blockState.get(ChestBlock.CHEST_TYPE) == ChestType.RIGHT) Collections.reverse(list);
        return list;
    }
}
