package hik1tka.risen_races.entity.zombie;

import hik1tka.risen_races.RisenRaces;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class ZombifiedHumanHuskEntity extends ZombifiedHumanEntity {

    public static final EntityType<ZombifiedHumanHuskEntity> ZOMBIFIED_HUMAN_HUSK = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(RisenRaces.MOD_ID, "zombified_human_husk"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ZombifiedHumanHuskEntity::new)
                    .dimensions(EntityDimensions.changing(0.6f, 1.95f))
                    .build()
    );

    /** Створює потрібний обєкт або сутність. */
    public static DefaultAttributeContainer.Builder createZombifiedHumanHuskAttributes() {


        return ZombifiedHumanEntity.createZombifiedHumanAttributes();
    }

    /** Повертає поточне значення властивості. */
    @Override
    public net.minecraft.util.Identifier getLootTableId() {
        return EntityType.HUSK.getLootTableId();
    }

    public ZombifiedHumanHuskEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    /** Перевіряє поточну умову. */
    @Override
    protected boolean isAffectedByDaylight() {
        return false;
    }

    /** Виконує спробу дії. */
    @Override
    public boolean tryAttack(Entity target) {
        boolean success = super.tryAttack(target);
        if (success && target instanceof LivingEntity livingTarget) {
            int durationTicks = this.getWorld().getDifficulty() == Difficulty.HARD ? 15 * 20 : 7 * 20;
            livingTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, durationTicks, 0));
        }
        return success;
    }

    /** Повертає поточне значення властивості. */
    @Override
    public float getSoundPitch() {
        if (this.isBaby()) {
            return (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F;
        }
        float base = super.getSoundPitch();
        return isFemale() ? base * 1.15f : base * 0.9f;
    }

}