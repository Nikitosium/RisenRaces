package hik1tka.risen_races.entity.humanoid;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public enum HumanoidRace {

    HUMAN(
            new Identifier("yourmod", "textures/entity/humanoid/human.png"),
            SoundEvents.ENTITY_VILLAGER_AMBIENT,
            SoundEvents.ENTITY_VILLAGER_HURT,
            SoundEvents.ENTITY_VILLAGER_DEATH,
            DangerBehavior.FLEE
    ),
    RIZEN_PIGLIN(
            new Identifier("yourmod", "textures/entity/humanoid/rizen_piglin.png"),
            SoundEvents.ENTITY_PIGLIN_AMBIENT,
            SoundEvents.ENTITY_PIGLIN_HURT,
            SoundEvents.ENTITY_PIGLIN_DEATH,
            DangerBehavior.FIGHT
    ),
    RYNAR(
            new Identifier("yourmod", "textures/entity/humanoid/rynar.png"),
            SoundEvents.ENTITY_VILLAGER_AMBIENT,
            SoundEvents.ENTITY_VILLAGER_HURT,
            SoundEvents.ENTITY_VILLAGER_DEATH,
            DangerBehavior.FLEE
    );

    private final Identifier texture;
    private final SoundEvent ambientSound;
    private final SoundEvent hurtSound;
    private final SoundEvent deathSound;
    private final DangerBehavior dangerBehavior;

    HumanoidRace(Identifier texture, SoundEvent ambientSound, SoundEvent hurtSound,
                 SoundEvent deathSound, DangerBehavior dangerBehavior) {
        this.texture = texture;
        this.ambientSound = ambientSound;
        this.hurtSound = hurtSound;
        this.deathSound = deathSound;
        this.dangerBehavior = dangerBehavior;
    }

    /** Повертає текстуру сутності. */
    public Identifier getTexture() {
        return texture;
    }

    /** Повертає поточне значення властивості. */
    public SoundEvent getAmbientSound() {
        return ambientSound;
    }

    /** Повертає поточне значення властивості. */
    public SoundEvent getHurtSound() {
        return hurtSound;
    }

    /** Повертає поточне значення властивості. */
    public SoundEvent getDeathSound() {
        return deathSound;
    }

    /** Повертає поточне значення властивості. */
    public DangerBehavior getDangerBehavior() {
        return dangerBehavior;
    }

    public enum DangerBehavior {
        FLEE,
        FIGHT
    }
}
