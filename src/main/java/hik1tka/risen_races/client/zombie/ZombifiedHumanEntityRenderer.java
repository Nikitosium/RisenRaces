package hik1tka.risen_races.client.zombie;

import hik1tka.risen_races.entity.zombie.ZombifiedHumanEntity;
import hik1tka.risen_races.mixin.BipedModelAccessor;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;





public class ZombifiedHumanEntityRenderer extends MobEntityRenderer<ZombifiedHumanEntity, ZombieEntityModel<ZombifiedHumanEntity>> {

    private static final Identifier TEXTURE =
            new Identifier("minecraft", "textures/entity/zombie/zombie.png");
    private static final float FEMALE_ARM_WIDTH_SCALE = 0.75F;
    private static final float CHILD_SCALE = 0.7F;

    public ZombifiedHumanEntityRenderer(EntityRendererFactory.Context context) {
        super(context, createModel(context.getPart(EntityModelLayers.ZOMBIE)), 0.5F);
    }

    private static ZombieEntityModel<ZombifiedHumanEntity> createModel(ModelPart root) {
        return new ZombieEntityModel<>(root) {
            /** Оновлює значення властивості. */
            @Override
            public void setAngles(ZombifiedHumanEntity entity, float limbAngle, float limbDistance,
                                  float animationProgress, float headYaw, float headPitch) {
                super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
                this.child = false;
            }
        };
    }

    /** Рендерить сутність. */
    @Override
    public void render(ZombifiedHumanEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {
        applyArmWidth(this.getModel(), entity.isFemale() ? FEMALE_ARM_WIDTH_SCALE : 1.0F);
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    /** Застосовує масштаб моделі. */
    @Override
    protected void scale(ZombifiedHumanEntity entity, MatrixStack matrices, float amount) {
        if (entity.isBaby()) {
            matrices.scale(CHILD_SCALE, CHILD_SCALE, CHILD_SCALE);
        }
    }

    private static void applyArmWidth(ZombieEntityModel<?> model, float widthScale) {
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
    public Identifier getTexture(ZombifiedHumanEntity entity) {
        return TEXTURE;
    }
}
