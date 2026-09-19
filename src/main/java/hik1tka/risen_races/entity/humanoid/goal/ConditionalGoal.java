package hik1tka.risen_races.entity.humanoid.goal;

import net.minecraft.entity.ai.goal.Goal;

import java.util.function.BooleanSupplier;

















public class ConditionalGoal extends Goal {

    private final Goal delegate;
    private final BooleanSupplier condition;

    public ConditionalGoal(Goal delegate, BooleanSupplier condition) {
        this.delegate = delegate;
        this.condition = condition;
        this.setControls(delegate.getControls());
    }

    /** Перевіряє поточну умову. */
    @Override
    public boolean canStart() {
        return condition.getAsBoolean() && delegate.canStart();
    }

    /** Виконує дію компонента. */
    @Override
    public boolean shouldContinue() {



        return condition.getAsBoolean() && delegate.shouldContinue();
    }

    /** Виконує дію компонента. */
    @Override
    public boolean shouldRunEveryTick() {
        return delegate.shouldRunEveryTick();
    }

    /** Виконує дію компонента. */
    @Override
    public void start() {
        delegate.start();
    }

    /** Виконує дію компонента. */
    @Override
    public void stop() {
        delegate.stop();
    }

    /** Оновлює стан сутності щотік. */
    @Override
    public void tick() {
        delegate.tick();
    }
}
