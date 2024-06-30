package folk.sisby.inventory_tabs.util;

import net.minecraft.util.math.Vec3d;

public class RaycastCache {
    public int ticksInvalid = 0;
    public boolean validThisTick = true;
    public Vec3d lastValidOffset = null;

    public void tick() {
        if (!validThisTick) ticksInvalid++;
        validThisTick = false;
    }

    public void hit(Vec3d offset) {
        lastValidOffset = offset;
        validThisTick = true;
        ticksInvalid = 0;
    }
}
