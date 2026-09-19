package hik1tka.risen_races.mixin;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;








@Mixin(BipedEntityModel.class)
public interface BipedModelAccessor {
    @Accessor("leftArm")
    ModelPart getLeftArm();

    @Accessor("rightArm")
    ModelPart getRightArm();
}
