package hik1tka.risen_races.mixin;

import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;








@Mixin(DrownedOverlayFeatureRenderer.class)
public interface DrownedOverlayModelAccessor {
    @Accessor("model")
    DrownedEntityModel<?> getModel();
}
