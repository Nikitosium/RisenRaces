package hik1tka.risen_races.entity.humanoid.goal;

import hik1tka.risen_races.entity.humanoid.risen_piglin.RisenPiglinEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;










public class FollowRescuerGoal extends Goal {

    private static final double FOLLOW_SPEED = 1.0D;

    private static final double STOP_DISTANCE_SQ = 3.0D * 3.0D;
    private static final double START_DISTANCE_SQ = 4.0D * 4.0D;

    private final RisenPiglinEntity piglin;
    @Nullable
    private LivingEntity followTarget;

    public FollowRescuerGoal(RisenPiglinEntity piglin) {
        this.piglin = piglin;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    /** Перевіряє поточну умову. */
    @Override
    public boolean canStart() {
        if (!isInNether() || !piglin.hasRescuer()) {
            return false;
        }
        followTarget = piglin.resolveFollowTarget();
        return followTarget != null
                && piglin.squaredDistanceTo(followTarget) > START_DISTANCE_SQ;
    }

    /** Виконує дію компонента. */
    @Override
    public boolean shouldContinue() {
        if (!isInNether() || !piglin.hasRescuer()) {
            return false;
        }
        followTarget = piglin.resolveFollowTarget();
        return followTarget != null
                && followTarget.isAlive()
                && piglin.squaredDistanceTo(followTarget) > STOP_DISTANCE_SQ;
    }

    /** Оновлює стан сутності щотік. */
    @Override
    public void tick() {
        if (followTarget == null) {
            return;
        }
        piglin.getLookControl().lookAt(followTarget);
        if (piglin.getNavigation().isIdle()) {
            piglin.getNavigation().startMovingTo(followTarget, FOLLOW_SPEED);
        }
    }

    /** Виконує дію компонента. */
    @Override
    public void stop() {
        followTarget = null;
        piglin.getNavigation().stop();
    }

    private boolean isInNether() {
        return piglin.getWorld().getRegistryKey() == World.NETHER;
    }
}
