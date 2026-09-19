package hik1tka.risen_races.entity.humanoid.goal;

import hik1tka.risen_races.entity.humanoid.HumanoidEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Box;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;







public class ShareFoodGoal extends Goal {

    private static final double DETECTION_RADIUS = 8.0D;
    private static final double MOVE_SPEED = 0.6D;
    private static final double SHARE_DISTANCE_SQ = 4.0D;

    private final HumanoidEntity giver;
    private HumanoidEntity recipient;

    public ShareFoodGoal(HumanoidEntity giver) {
        this.giver = giver;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    /** Перевіряє поточну умову. */
    @Override
    public boolean canStart() {
        if (giver.getTotalFoodItemCount() < 2) {
            return false;
        }

        Box box = giver.getBoundingBox().expand(DETECTION_RADIUS);
        List<HumanoidEntity> candidates = giver.getWorld().getEntitiesByClass(
                HumanoidEntity.class, box,
                other -> other != giver
                        && other.isAlive()
                        && other.getTotalFoodItemCount() < giver.getTotalFoodItemCount());

        recipient = candidates.stream()
                .min(Comparator.comparingInt(HumanoidEntity::getTotalFoodItemCount)
                        .thenComparingDouble(giver::squaredDistanceTo))
                .orElse(null);
        return recipient != null;
    }

    /** Виконує дію компонента. */
    @Override
    public boolean shouldContinue() {
        return recipient != null
                && recipient.isAlive()
                && giver.getTotalFoodItemCount() >= 2
                && recipient.getTotalFoodItemCount() < giver.getTotalFoodItemCount();
    }

    /** Оновлює стан сутності щотік. */
    @Override
    public void tick() {
        if (recipient == null) return;
        giver.getNavigation().startMovingTo(recipient, MOVE_SPEED);
        if (giver.squaredDistanceTo(recipient) <= SHARE_DISTANCE_SQ) {
            giver.shareOneFoodItemWith(recipient);
            recipient = null;
        }
    }

    /** Виконує дію компонента. */
    @Override
    public void stop() {
        recipient = null;
        giver.getNavigation().stop();
    }
}
