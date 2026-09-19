package hik1tka.risen_races.entity.humanoid.goal;

import hik1tka.risen_races.entity.humanoid.HumanoidEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Box;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;









public class PickUpFoodGoal extends Goal {

    private static final double DETECTION_RADIUS = 8.0D;
    private static final double MOVE_SPEED = 0.6D;

    private final HumanoidEntity humanoid;
    private ItemEntity targetItem;

    public PickUpFoodGoal(HumanoidEntity humanoid) {
        this.humanoid = humanoid;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    /** Перевіряє поточну умову. */
    @Override
    public boolean canStart() {


        if (humanoid.hasEnoughFoodToBreed()) {
            return false;
        }
        Box box = humanoid.getBoundingBox().expand(DETECTION_RADIUS);
        List<ItemEntity> items = humanoid.getWorld().getEntitiesByClass(
                ItemEntity.class, box,
                e -> e.isAlive()
                        && !e.getStack().isEmpty()
                        && HumanoidEntity.isBreedingFood(e.getStack().getItem())
                        && humanoid.canSee(e));
        targetItem = items.stream()
                .min(Comparator.comparingDouble(humanoid::squaredDistanceTo))
                .orElse(null);
        return targetItem != null;
    }

    /** Виконує дію компонента. */
    @Override
    public boolean shouldContinue() {
        return targetItem != null
                && targetItem.isAlive()
                && !targetItem.getStack().isEmpty()
                && !humanoid.hasEnoughFoodToBreed();
    }

    /** Оновлює стан сутності щотік. */
    @Override
    public void tick() {
        if (targetItem == null) {
            return;
        }
        humanoid.getNavigation().startMovingTo(targetItem, MOVE_SPEED);
    }

    /** Виконує дію компонента. */
    @Override
    public void stop() {
        targetItem = null;
        humanoid.getNavigation().stop();
    }
}
