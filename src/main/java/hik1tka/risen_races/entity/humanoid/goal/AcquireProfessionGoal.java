package hik1tka.risen_races.entity.humanoid.goal;

import hik1tka.risen_races.entity.humanoid.HumanoidEntity;
import hik1tka.risen_races.entity.humanoid.data.ProfessionDefinition;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;











public class AcquireProfessionGoal extends Goal {

    private static final int RESCAN_COOLDOWN_TICKS = 200;
    private static final int GIVE_UP_TICKS = 600;

    private final HumanoidEntity entity;
    private int cooldown = 0;

    private BlockPos targetPos;
    private String targetProfession;
    private int pathingTicks;

    public AcquireProfessionGoal(HumanoidEntity entity) {
        this.entity = entity;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    /** Перевіряє поточну умову. */
    @Override
    public boolean canStart() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        if (!"none".equals(entity.getProfession())) return false;
        if (entity.isBaby()) return false;
        if (!(entity.getWorld() instanceof ServerWorld world)) return false;

        List<ProfessionDefinition> professions = entity.getAvailableProfessions();
        if (professions.isEmpty()) return false;

        PointOfInterestStorage poiStorage = world.getPointOfInterestStorage();

        for (ProfessionDefinition profession : professions) {
            Optional<BlockPos> found = poiStorage.getNearestPosition(
                    profession.workstationPredicate(),
                    entity.getBlockPos(),
                    profession.searchRadius(),
                    PointOfInterestStorage.OccupationStatus.HAS_SPACE
            );

            if (found.isPresent() && !isClaimedByOtherHumanoid(world, found.get())) {
                this.targetPos = found.get();
                this.targetProfession = profession.id();
                return true;
            }
        }

        cooldown = RESCAN_COOLDOWN_TICKS;
        return false;
    }

    /** Виконує дію компонента. */
    @Override
    public boolean shouldContinue() {
        return targetPos != null
                && "none".equals(entity.getProfession())
                && pathingTicks < GIVE_UP_TICKS;
    }

    /** Виконує дію компонента. */
    @Override
    public void start() {
        pathingTicks = 0;
        moveToTarget();
    }

    /** Оновлює стан сутності щотік. */
    @Override
    public void tick() {
        pathingTicks++;
        if (entity.getNavigation().isIdle()) {
            moveToTarget();
        }

        if (targetPos != null && entity.squaredDistanceTo(
                targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5) < 4.0D) {
            tryClaim();
        }
    }

    /** Виконує дію компонента. */
    @Override
    public void stop() {
        targetPos = null;
        targetProfession = null;
        cooldown = RESCAN_COOLDOWN_TICKS;
    }

    private void moveToTarget() {
        if (targetPos == null) return;
        entity.getNavigation().startMovingTo(
                targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, 0.5D);
    }

    private void tryClaim() {
        if (!(entity.getWorld() instanceof ServerWorld world)) return;



        if (isClaimedByOtherHumanoid(world, targetPos)) {
            this.targetPos = null;
            this.targetProfession = null;
            cooldown = RESCAN_COOLDOWN_TICKS;
            return;
        }

        entity.setProfession(targetProfession);
        entity.setJobSite(targetPos);
    }

    private boolean isClaimedByOtherHumanoid(ServerWorld world, BlockPos pos) {
        return !world.getEntitiesByClass(
                HumanoidEntity.class,
                new net.minecraft.util.math.Box(pos).expand(1.0D),
                other -> other != entity && pos.equals(other.getJobSite())
        ).isEmpty();
    }
}
