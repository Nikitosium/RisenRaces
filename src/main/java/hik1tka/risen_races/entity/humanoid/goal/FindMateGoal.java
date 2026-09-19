package hik1tka.risen_races.entity.humanoid.goal;

import hik1tka.risen_races.entity.humanoid.HumanoidEntity;
import hik1tka.risen_races.util.VillageCapacityHelper;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Box;

import java.util.EnumSet;
import java.util.List;







public class FindMateGoal extends Goal {

    private static final double SEARCH_RADIUS = 8.0D;
    private static final double BREED_DISTANCE_SQ = 3.0D * 3.0D;

    private final HumanoidEntity self;
    private HumanoidEntity target;

    public FindMateGoal(HumanoidEntity self) {
        this.self = self;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    /** Перевіряє поточну умову. */
    @Override
    public boolean canStart() {
        if (!self.isBreedingReady()) return false;
        if (!VillageCapacityHelper.hasRoomToBreed(self)) return false;

        Box box = self.getBoundingBox().expand(SEARCH_RADIUS);
        List<HumanoidEntity> candidates = self.getWorld().getEntitiesByClass(
                HumanoidEntity.class, box, self::canBreedWith);

        if (candidates.isEmpty()) return false;

        target = candidates.get(self.getRandom().nextInt(candidates.size()));
        return true;
    }

    /** Виконує дію компонента. */
    @Override
    public boolean shouldContinue() {
        return target != null && target.isAlive() && self.canBreedWith(target) && self.isBreedingReady();
    }

    /** Оновлює стан сутності щотік. */
    @Override
    public void tick() {
        if (target == null) return;

        self.getNavigation().startMovingTo(target, 0.6D);

        if (self.squaredDistanceTo(target) <= BREED_DISTANCE_SQ) {
            self.breedWith(target);
            target = null;
        }
    }

    /** Виконує дію компонента. */
    @Override
    public void stop() {
        target = null;
    }
}
