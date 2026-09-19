package hik1tka.risen_races.client.humanoid.human;

import hik1tka.risen_races.entity.humanoid.human.HumanEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class NPCClothingFeatureRenderer extends FeatureRenderer<HumanEntity, PlayerEntityModel<HumanEntity>> {
    public NPCClothingFeatureRenderer(FeatureRendererContext<HumanEntity, PlayerEntityModel<HumanEntity>> context) {
        super(context);
    }

    /** Рендерить сутність. */
    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, HumanEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity.isInvisible() || "none".equals(entity.getHumanoidData().getProfession())) return;

        String prof = entity.getHumanoidData().getProfession();


        if (prof.equals("farmer")) {
            return;
        }


        Identifier professionId = new Identifier("risen_races", "textures/entity/human/profession/" + prof + ".png");


        Identifier levelId = new Identifier("minecraft", "textures/entity/villager/profession_level/stone.png");


        renderModel(this.getContextModel(), professionId, matrices, vertexConsumers, light, entity, 1.0F, 1.0F, 1.0F);



    }
}
