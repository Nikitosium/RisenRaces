package hik1tka.risen_races.client.humanoid.piglin;

import hik1tka.risen_races.client.humanoid.human.FarmerHatFeatureRenderer;
import hik1tka.risen_races.client.humanoid.human.FishermanHatFeatureRenderer;
import hik1tka.risen_races.client.humanoid.human.model.profession.hat.FarmerHatModel;
import hik1tka.risen_races.client.humanoid.human.model.profession.hat.FishermanHatModel;
import hik1tka.risen_races.client.humanoid.piglin.model.RisenFemalePiglinModel;
import hik1tka.risen_races.entity.humanoid.risen_piglin.RisenPiglinEntity;
import hik1tka.risen_races.register.ModModelLayers;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class RisenPiglinEntityRender extends MobEntityRenderer<RisenPiglinEntity, EntityModel<RisenPiglinEntity>> {

    private static final Identifier TEXTURE =
            new Identifier("minecraft", "textures/entity/piglin/piglin.png");

    private final PiglinEntityModel<RisenPiglinEntity> maleModel;
    private final RisenFemalePiglinModel<RisenPiglinEntity> femaleModel;

    public RisenPiglinEntityRender(EntityRendererFactory.Context context) {
        super(context, createMaleModel(context.getPart(EntityModelLayers.PIGLIN)), 0.3F);
        this.maleModel = (PiglinEntityModel<RisenPiglinEntity>) this.model;
        this.femaleModel = new RisenFemalePiglinModel<>(context.getPart(ModModelLayers.RISEN_PIGLIN_FEMALE));
        this.addFeature(new RisenPiglinClothingFeatureRenderer(this));
        this.addFeature(new FarmerHatFeatureRenderer<>(this, new FarmerHatModel<>(context.getPart(ModModelLayers.FARMER_HAT))));
        this.addFeature(new FishermanHatFeatureRenderer<>(this, new FishermanHatModel<>(context.getPart(ModModelLayers.FISHERMAN_HAT))));
    }











    private static PiglinEntityModel<RisenPiglinEntity> createMaleModel(net.minecraft.client.model.ModelPart root) {
        return new PiglinEntityModel<>(root) {
            /** Оновлює значення властивості. */
            @Override
            public void setAngles(RisenPiglinEntity entity, float limbAngle, float limbDistance,
                                  float animationProgress, float headYaw, float headPitch) {
                super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
                this.child = false;
            }
        };
    }

    /** Рендерить сутність. */
    @Override
    public void render(RisenPiglinEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {
        this.model = entity.isFemale() ? this.femaleModel : this.maleModel;
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    /** Повертає текстуру сутності. */
    @Override
    public Identifier getTexture(RisenPiglinEntity entity) {



        return TEXTURE;
    }

    /** Застосовує масштаб моделі. */
    @Override
    protected void scale(RisenPiglinEntity entity, MatrixStack matrices, float amount) {
        float f = entity.getScaleFactor();
        matrices.scale(f, f, f);
    }
}
