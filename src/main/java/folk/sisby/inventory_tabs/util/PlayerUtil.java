package folk.sisby.inventory_tabs.util;

import folk.sisby.inventory_tabs.InventoryTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.List;

public class PlayerUtil {
    public static final int REACH = 5;
    public static final double BLOCK_REACH_SQUARE = REACH * REACH;
    public static final List<Vec3d> BLOCK_OFFSETS = List.of(
            new Vec3d(0.5D, 0.5D, 0.5D),
            new Vec3d(0.2D, 0.2D, 0.2D),
            new Vec3d(0.8D, 0.2D, 0.2D),
            new Vec3d(0.2D, 0.8D, 0.2D),
            new Vec3d(0.2D, 0.2D, 0.8D),
            new Vec3d(0.8D, 0.8D, 0.2D),
            new Vec3d(0.2D, 0.8D, 0.8D),
            new Vec3d(0.8D, 0.2D, 0.8D),
            new Vec3d(0.8D, 0.8D, 0.8D)
    );

    public static boolean inRange(PlayerEntity player, BlockPos pos) {
        if (Vec3d.of(pos).add(0.5D, 0.5D, 0.5D).squaredDistanceTo(player.getEyePos()) > BLOCK_REACH_SQUARE) return false;
        BlockHitResult result = raycast(player, pos);
        return pos.equals(result.getBlockPos());
    }

    public static boolean inRange(PlayerEntity player, Entity entity) {
        if (entity.getPos().squaredDistanceTo(player.getEyePos()) > BLOCK_REACH_SQUARE) return false;
        if (InventoryTabs.CONFIG.ignoreWalls) return true;
        EntityHitResult result = raycast(player, entity);
        return result != null && entity.equals(result.getEntity());
    }

    public static BlockHitResult raycast(PlayerEntity player, BlockPos pos) {
        for (Vec3d offset : BLOCK_OFFSETS) {
            BlockHitResult hitResult = player.getWorld().raycast(new RaycastContext(player.getEyePos(), Vec3d.of(pos).add(offset), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player));
            if (hitResult.getType() != HitResult.Type.MISS && hitResult.getBlockPos().equals(pos)) return hitResult;
        }
        return BlockHitResult.createMissed(player.getPos(), Direction.EAST, player.getBlockPos());
    }

    public static EntityHitResult raycast(PlayerEntity player, Entity entity) {
        return ProjectileUtil.raycast(player, player.getEyePos(), entity.getPos(), player.getBoundingBox().stretch(entity.getRotationVec(1.0F).multiply(REACH)).expand(1.0, 1.0, 1.0), e -> true, BLOCK_REACH_SQUARE);
    }
}
