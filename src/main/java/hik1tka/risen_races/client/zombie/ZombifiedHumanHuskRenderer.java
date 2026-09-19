package hik1tka.risen_races.client.zombie;

import hik1tka.risen_races.entity.zombie.ZombifiedHumanHuskEntity;
import hik1tka.risen_races.mixin.BipedModelAccessor;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/**
 * Використовує спільну ванільну модель Husk; у жіночого варіанта рука
 * має slim-ширину: 3/4 від wide-руки.
 * TODO: якщо EntityModelLayers.HUSK відсутній у твоєму мапінгу - звір
 * точну назву константи в декомпільованому EntityModelLayers.
 */
public class ZombifiedHumanHuskRenderer extends MobEntityRenderer<ZombifiedHumanHuskEntity, ZombieEntityModel<ZombifiedHumanHuskEntity>> {

    private static final Identifier TEXTURE =
            new Identifier("minecraft", "textures/entity/zombie/husk.png");
    private static final float FEMALE_ARM_WIDTH_SCALE = 0.75F;
    private static final float CHILD_SCALE = 0.7F;

    public ZombifiedHumanHuskRenderer(EntityRendererFactory.Context context) {
        super(context, createModel(context.getPart(EntityModelLayers.HUSK)), 0.5F);
    }

    private static ZombieEntityModel<ZombifiedHumanHuskEntity> createModel(ModelPart root) {
        return new ZombieEntityModel<>(root) {
            @Override
            public void setAngles(ZombifiedHumanHuskEntity entity, float limbAngle, float limbDistance,
                                  float animationProgress, float headYaw, float headPitch) {
                super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
                this.child = false;
            }
        };
    }

    @Override
    public void render(ZombifiedHumanHuskEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {
        applyArmWidth(this.getModel(), entity.isFemale() ? FEMALE_ARM_WIDTH_SCALE : 1.0F);
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    protected void scale(ZombifiedHumanHuskEntity entity, MatrixStack matrices, float amount) {
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

    @Override
    public Identifier getTexture(ZombifiedHumanHuskEntity entity) {
        return TEXTURE;
    }
}
