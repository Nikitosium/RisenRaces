package hik1tka.risen_races.util;

import hik1tka.risen_races.register.ModEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class EffectAnnihilator {

    /** Виконує дію компонента. */
    public static void checkAndAnnihilate(LivingEntity entity) {
        if (entity.getWorld().isClient) return;

        if (entity.hasStatusEffect(ModEffect.PURIFICATION) && entity.hasStatusEffect(ModEffect.ZOMBIFICATION)) {

            entity.removeStatusEffect(ModEffect.PURIFICATION);
            entity.removeStatusEffect(ModEffect.ZOMBIFICATION);

            if (entity.hasStatusEffect(ModEffect.PURIFICATION)) {
                entity.removeStatusEffect(ModEffect.PURIFICATION);
            }

            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 1));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 200, 0));

            entity.getWorld().playSound(null, entity.getBlockPos(),
                    SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.NEUTRAL, 0.8f, 1.5f);
        }
    }
}
