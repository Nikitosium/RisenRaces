package hik1tka.risen_races.entity.humanoid.goal;

import hik1tka.risen_races.entity.humanoid.risen_piglin.RisenPiglinEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;















public class SeekNetherPortalGoal extends Goal {

    private static final int SEARCH_RADIUS_XZ = 24;
    private static final int SEARCH_RADIUS_Y = 8;
    private static final int RESCAN_INTERVAL_TICKS = 40;
    private static final double SAFE_STOP_DISTANCE = 2.0D;
    private static final double ARRIVE_DISTANCE_SQ = 1.5D * 1.5D;
    private static final int GIVE_UP_STUCK_TICKS = 60;

    private final RisenPiglinEntity piglin;
    private BlockPos portalPos;
    private int rescanCooldown;
    private int stuckTicks;
    private boolean onStandoff;

    public SeekNetherPortalGoal(RisenPiglinEntity piglin) {
        this.piglin = piglin;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    /** Перевіряє поточну умову. */
    @Override
    public boolean canStart() {
        if (!isInNether()) return false;
        if (!piglin.hasRescuer()) return false;
        if (rescanCooldown > 0) {
            rescanCooldown--;
            return false;
        }
        rescanCooldown = RESCAN_INTERVAL_TICKS;
        portalPos = findNearestPortalBlock();
        return portalPos != null;
    }

    /** Виконує дію компонента. */
    @Override
    public boolean shouldContinue() {
        if (portalPos == null || !isInNether()) return false;
        if (!piglin.getWorld().getBlockState(portalPos).isOf(Blocks.NETHER_PORTAL)) {

            return false;
        }
        return piglin.squaredDistanceTo(
                portalPos.getX() + 0.5, portalPos.getY(), portalPos.getZ() + 0.5) > ARRIVE_DISTANCE_SQ;
    }

    /** Виконує дію компонента. */
    @Override
    public void start() {
        stuckTicks = 0;
        onStandoff = false;
        moveToward(portalPos);
    }

    /** Оновлює стан сутності щотік. */
    @Override
    public void tick() {
        piglin.getLookControl().lookAt(
                portalPos.getX() + 0.5, portalPos.getY() + 0.5, portalPos.getZ() + 0.5);

        if (piglin.getNavigation().isIdle()) {
            stuckTicks++;
            if (stuckTicks > GIVE_UP_STUCK_TICKS) {
                if (!onStandoff) {
                    moveToSafeStandoff();
                    onStandoff = true;
                }

                return;
            }
            moveToward(portalPos);
        } else {
            stuckTicks = 0;
            onStandoff = false;
        }
    }

    /** Виконує дію компонента. */
    @Override
    public void stop() {
        portalPos = null;
        piglin.getNavigation().stop();
    }

    private boolean isInNether() {
        return piglin.getWorld().getRegistryKey() == World.NETHER;
    }

    private void moveToward(BlockPos pos) {
        piglin.getNavigation().startMovingTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 1.2D);
    }





    private void moveToSafeStandoff() {
        Vec3d portalCenter = Vec3d.ofCenter(portalPos);
        Vec3d toPortal = portalCenter.subtract(piglin.getPos());
        if (toPortal.lengthSquared() < 1.0E-4) {
            return;
        }
        Vec3d direction = toPortal.normalize();
        Vec3d standoff = portalCenter.subtract(direction.multiply(SAFE_STOP_DISTANCE));
        piglin.getNavigation().startMovingTo(standoff.x, standoff.y, standoff.z, 1.0D);
    }

    @Nullable
    private BlockPos findNearestPortalBlock() {
        BlockPos center = piglin.getBlockPos();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockPos closest = null;
        long closestDistSq = Long.MAX_VALUE;

        for (int dx = -SEARCH_RADIUS_XZ; dx <= SEARCH_RADIUS_XZ; dx++) {
            for (int dy = -SEARCH_RADIUS_Y; dy <= SEARCH_RADIUS_Y; dy++) {
                for (int dz = -SEARCH_RADIUS_XZ; dz <= SEARCH_RADIUS_XZ; dz++) {
                    mutable.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    if (piglin.getWorld().getBlockState(mutable).isOf(Blocks.NETHER_PORTAL)) {
                        long d = (long) dx * dx + (long) dy * dy + (long) dz * dz;
                        if (d < closestDistSq) {
                            closestDistSq = d;
                            closest = mutable.toImmutable();
                        }
                    }
                }
            }
        }





        return closest;
    }
}
