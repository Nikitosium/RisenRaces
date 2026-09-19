package hik1tka.risen_races.client.humanoid.piglin;

import hik1tka.risen_races.entity.humanoid.risen_piglin.RisenPiglinEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;








public class RisenPiglinClothingFeatureRenderer extends FeatureRenderer<RisenPiglinEntity, EntityModel<RisenPiglinEntity>> {

    public RisenPiglinClothingFeatureRenderer(FeatureRendererContext<RisenPiglinEntity, EntityModel<RisenPiglinEntity>> context) {
        super(context);
    }

    /** Рендерить сутність. */
    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, RisenPiglinEntity entity,
                       float limbAngle, float limbDistance, float tickDelta, float animationProgress,
                       float headYaw, float headPitch) {
        String prof = entity.getProfession();
        if (entity.isInvisible() || "none".equals(prof)) return;





        if ("farmer".equals(prof) || "fisherman".equals(prof)) return;



        Identifier professionId = new Identifier("risen_races",
                "textures/entity/human/profession/" + prof + ".png");

        renderModel(this.getContextModel(), professionId, matrices, vertexConsumers, light, entity, 1.0F, 1.0F, 1.0F);
    }
}
