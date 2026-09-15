package hik1tka.risen_races.mixin;

import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * DrownedOverlayFeatureRenderer будує СВІЙ ВЛАСНИЙ окремий екземпляр
 * DrownedEntityModel у конструкторі (не той, що в базового рендерера) -
 * тому скейл рук, виставлений на основній моделі, на цей шар не впливає.
 * Цей акцесор дає доступ саме до внутрішньої моделі шару, щоб скейлити
 * руки і тут теж (див. ZombifiedHumanDrownedRenderer).
 */
@Mixin(DrownedOverlayFeatureRenderer.class)
public interface DrownedOverlayModelAccessor {
    @Accessor("model")
    DrownedEntityModel<?> getModel();
}
