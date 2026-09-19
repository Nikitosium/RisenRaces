package hik1tka.risen_races.entity.humanoid.goal;

import hik1tka.risen_races.entity.humanoid.HumanoidEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.Box;

import java.util.EnumSet;
import java.util.List;

public class RizenPiglinDefenseGoal extends Goal {

    private static final double DETECTION_RADIUS = 12.0D;
    private static final double ALLY_CALL_RADIUS = 24.0D;
    private static final int SOLO_FIGHT_THRESHOLD = 2;

    private final HumanoidEntity piglin;
    private List<HostileEntity> nearbyThreats;

    public RizenPiglinDefenseGoal(HumanoidEntity piglin) {
        this.piglin = piglin;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    /** Перевіряє поточну умову. */
    @Override
    public boolean canStart() {
        Box searchBox = piglin.getBoundingBox().expand(DETECTION_RADIUS);
        nearbyThreats = piglin.getWorld().getEntitiesByClass(
                HostileEntity.class, searchBox, e -> e.isAlive() && piglin.canSee(e));
        return !nearbyThreats.isEmpty();
    }

    /** Виконує дію компонента. */
    @Override
    public boolean shouldContinue() {
        return piglin.getTarget() != null && piglin.getTarget().isAlive();
    }

    /** Виконує дію компонента. */
    @Override
    public void start() {
        if (nearbyThreats.size() <= SOLO_FIGHT_THRESHOLD) {
            fightSolo();
        } else {
            callForHelp();
        }
    }

    private void fightSolo() {
        HostileEntity closest = nearbyThreats.get(0);
        double closestDist = piglin.squaredDistanceTo(closest);
        for (HostileEntity threat : nearbyThreats) {
            double d = piglin.squaredDistanceTo(threat);
            if (d < closestDist) {
                closest = threat;
                closestDist = d;
            }
        }
        piglin.setTarget(closest);
    }

    private void callForHelp() {

        piglin.getWorld().playSound(null, piglin.getBlockPos(),
                net.minecraft.sound.SoundEvents.ENTITY_PIGLIN_ANGRY,
                net.minecraft.sound.SoundCategory.NEUTRAL, 1.0F, 1.0F);
        fightSolo();
        Box allyBox = piglin.getBoundingBox().expand(ALLY_CALL_RADIUS);
        List<HumanoidEntity> allies = piglin.getWorld().getEntitiesByClass(
                HumanoidEntity.class, allyBox,
                e -> e != piglin && e.getRace() == piglin.getRace());
        for (HumanoidEntity ally : allies) {
            if (piglin.getTarget() != null) {
                ally.setTarget(piglin.getTarget());
            }
        }
    }
}
