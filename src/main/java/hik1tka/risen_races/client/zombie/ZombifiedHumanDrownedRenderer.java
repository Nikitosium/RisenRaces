package hik1tka.risen_races.client.zombie;

import hik1tka.risen_races.entity.zombie.ZombifiedHumanDrownedEntity;
import hik1tka.risen_races.mixin.BipedModelAccessor;
import hik1tka.risen_races.mixin.DrownedOverlayModelAccessor;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;















public class ZombifiedHumanDrownedRenderer extends MobEntityRenderer<ZombifiedHumanDrownedEntity, DrownedEntityModel<ZombifiedHumanDrownedEntity>> {

    private static final Identifier TEXTURE =
            new Identifier("minecraft", "textures/entity/zombie/drowned.png");
    private static final float FEMALE_ARM_WIDTH_SCALE = 0.75F;
    private static final float CHILD_SCALE = 0.7F;

    private final DrownedOverlayFeatureRenderer<ZombifiedHumanDrownedEntity> outerLayer;

    public ZombifiedHumanDrownedRenderer(EntityRendererFactory.Context context) {
        super(context, createModel(context.getPart(EntityModelLayers.DROWNED)), 0.5F);




        this.outerLayer = new DrownedOverlayFeatureRenderer<>(this, context.getModelLoader());
        this.addFeature(this.outerLayer);
    }

    private static DrownedEntityModel<ZombifiedHumanDrownedEntity> createModel(ModelPart root) {
        return new DrownedEntityModel<>(root) {
            /** Оновлює значення властивості. */
            @Override
            public void setAngles(ZombifiedHumanDrownedEntity entity, float limbAngle, float limbDistance,
                                  float animationProgress, float headYaw, float headPitch) {
                super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
                this.child = false;
            }
        };
    }

    /** Рендерить сутність. */
    @Override
    public void render(ZombifiedHumanDrownedEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {
        float armWidth = entity.isFemale() ? FEMALE_ARM_WIDTH_SCALE : 1.0F;
        applyArmWidth(this.getModel(), armWidth);
        applyArmWidth(((DrownedOverlayModelAccessor) this.outerLayer).getModel(), armWidth);
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    /** Застосовує масштаб моделі. */
    @Override
    protected void scale(ZombifiedHumanDrownedEntity entity, MatrixStack matrices, float amount) {
        if (entity.isBaby()) {
            matrices.scale(CHILD_SCALE, CHILD_SCALE, CHILD_SCALE);
        }
    }

    private static void applyArmWidth(Object model, float widthScale) {
        BipedModelAccessor accessor = (BipedModelAccessor) model;
        ModelPart leftArm = accessor.getLeftArm();
        ModelPart rightArm = accessor.getRightArm();
        leftArm.xScale = widthScale;
        rightArm.xScale = widthScale;
        leftArm.yScale = rightArm.yScale = 1.0F;
        leftArm.zScale = rightArm.zScale = 1.0F;
    }

    /** Повертає текстуру сутності. */
    @Override
    public Identifier getTexture(ZombifiedHumanDrownedEntity entity) {
        return TEXTURE;
    }
}
