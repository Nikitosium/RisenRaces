package hik1tka.risen_races.entity.humanoid.goal;

import hik1tka.risen_races.entity.humanoid.HumanoidEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class PanicUntilSafeGoal extends Goal {
    private final HumanoidEntity entity;
    private final double speed;
    private LivingEntity lastAttacker;

    private int safeTimer = 0;
    private final int maxSafeTicks;

    public PanicUntilSafeGoal(HumanoidEntity entity, double speed, int safeSeconds) {
        this.entity = entity;
        this.speed = speed;
        this.maxSafeTicks = safeSeconds * 20;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    /** Перевіряє поточну умову. */
    @Override
    public boolean canStart() {

        LivingEntity attacker = this.entity.getAttacker();
        if (attacker != null && this.entity.getLastAttackedTime() > 0) {
            this.lastAttacker = attacker;
            this.safeTimer = 0;
            return true;
        }
        return false;
    }

    /** Виконує дію компонента. */
    @Override
    public boolean shouldContinue() {
        if (this.lastAttacker == null || !this.lastAttacker.isAlive()) {
            return false;
        }

        if (this.lastAttacker.hasStatusEffect(StatusEffects.INVISIBILITY)) {
            double armorFactor = this.lastAttacker. getArmorVisibility();
            if (armorFactor <= 0.0D) {
                this.safeTimer++;
                return this.safeTimer < this.maxSafeTicks;
            }
        }


        boolean canSeeAttacker = this.entity.getVisibilityCache().canSee(this.lastAttacker)
                && this.entity.squaredDistanceTo(this.lastAttacker) < 1024.0;

        if (canSeeAttacker) {
            this.safeTimer = 0;
            return true;
        } else {
            this.safeTimer++;
            return this.safeTimer < this.maxSafeTicks;
        }
    }

    /** Виконує дію компонента. */
    @Override
    public void start() {
        this.findAndMoveAway();
    }

    /** Оновлює стан сутності щотік. */
    @Override
    public void tick() {

        if (this.entity.getNavigation().isIdle()) {
            this.findAndMoveAway();
        }
    }

    /** Виконує дію компонента. */
    @Override
    public void stop() {
        this.lastAttacker = null;
        this.entity.setAttacker(null);
        this.safeTimer = 0;
    }

    private void findAndMoveAway() {
        if (this.lastAttacker == null) return;


        Vec3d target = NoPenaltyTargeting.findFrom(this.entity, 16, 7, this.lastAttacker.getPos());
        if (target != null) {
            this.entity.getNavigation().startMovingTo(target.x, target.y, target.z, this.speed);
        }
    }
}
