package hik1tka.risen_races.entity.humanoid.goal;

import hik1tka.risen_races.entity.humanoid.HumanoidEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class LowHealthFleeGoal extends Goal {

    private static final float HEALTH_THRESHOLD = 5.0f;
    private static final double FLEE_SPEED = 1.4D;
    private static final double FLEE_DISTANCE = 10.0D;
    private static final int RECENT_ATTACK_TICKS = 100;

    private final HumanoidEntity entity;

    public LowHealthFleeGoal(HumanoidEntity entity) {
        this.entity = entity;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    /** Перевіряє поточну умову. */
    @Override
    public boolean canStart() {
        LivingEntity attacker = entity.getAttacker();
        return entity.getHealth() < HEALTH_THRESHOLD
                && attacker != null
                && attacker.isAlive()
                && entity.getWorld().getTime() - entity.getLastAttackedTime() < RECENT_ATTACK_TICKS;
    }

    /** Виконує дію компонента. */
    @Override
    public boolean shouldContinue() {
        return canStart();
    }

    /** Виконує дію компонента. */
    @Override
    public void start() {
        recalculateFleePoint();
    }

    /** Оновлює стан сутності щотік. */
    @Override
    public void tick() {


        if (entity.getNavigation().isIdle()) {
            recalculateFleePoint();
        }
    }

    /** Виконує дію компонента. */
    @Override
    public void stop() {
        entity.getNavigation().stop();
    }

    private void recalculateFleePoint() {
        LivingEntity attacker = entity.getAttacker();
        if (attacker == null) return;

        Vec3d fromAttacker = entity.getPos().subtract(attacker.getPos());
        if (fromAttacker.lengthSquared() < 1.0E-4) {

            fromAttacker = new Vec3d(entity.getRandom().nextDouble() - 0.5, 0, entity.getRandom().nextDouble() - 0.5);
        }
        Vec3d direction = fromAttacker.normalize();
        Vec3d fleeTarget = entity.getPos().add(direction.multiply(FLEE_DISTANCE));

        entity.getNavigation().startMovingTo(fleeTarget.x, fleeTarget.y, fleeTarget.z, FLEE_SPEED);
    }
}
