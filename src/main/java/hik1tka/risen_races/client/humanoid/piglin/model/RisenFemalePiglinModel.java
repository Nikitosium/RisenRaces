package hik1tka.risen_races.client.humanoid.piglin.model;

import hik1tka.risen_races.entity.humanoid.risen_piglin.RisenPiglinEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

















public class RisenFemalePiglinModel<T extends RisenPiglinEntity> extends SinglePartEntityModel<T> implements net.minecraft.client.render.entity.model.ModelWithHead {

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart leftEar;
    private final ModelPart rightEar;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public RisenFemalePiglinModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.head = body.getChild("head");
        this.leftEar = head.getChild("leftear");
        this.rightEar = head.getChild("rightear");
        this.rightArm = body.getChild("rightarm");
        this.leftArm = body.getChild("leftarm");
        this.rightLeg = body.getChild("rightleg");
        this.leftLeg = body.getChild("leftleg");
    }

    /** Повертає поточне значення властивості. */
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        ModelPartData body = root.addChild("body", ModelPartBuilder.create()
                        .uv(16, 16).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F)
                        .uv(16, 32).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.25F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));




        ModelPartData head = body.addChild("head", ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, new Dilation(-0.02F))
                        .uv(31, 1).cuboid(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 1.0F, new Dilation(-0.02F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        head.addChild("leftear", ModelPartBuilder.create()
                        .uv(51, 6).cuboid(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F),
                ModelTransform.of(5.0F, -6.0F, 0.0F, 0.0F, 0.0F, -0.5236F));

        head.addChild("rightear", ModelPartBuilder.create()
                        .uv(39, 6).cuboid(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F),
                ModelTransform.of(-5.0F, -6.0F, 0.0F, 0.0F, 0.0F, 0.5236F));


        body.addChild("rightarm", ModelPartBuilder.create()
                        .uv(41, 16).cuboid(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F)
                        .uv(40, 32).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.25F)),
                ModelTransform.pivot(-5.0F, 2.0F, 0.0F));

        body.addChild("leftarm", ModelPartBuilder.create()
                        .uv(32, 48).cuboid(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F)
                        .uv(48, 48).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.25F)),
                ModelTransform.pivot(5.0F, 2.0F, 0.0F));

        body.addChild("rightleg", ModelPartBuilder.create()
                        .uv(0, 16).cuboid(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F)
                        .uv(0, 32).cuboid(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.25F)),
                ModelTransform.pivot(-1.9F, 12.0F, 0.0F));

        body.addChild("leftleg", ModelPartBuilder.create()
                        .uv(16, 48).cuboid(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F)
                        .uv(0, 48).cuboid(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.25F)),
                ModelTransform.pivot(1.9F, 12.0F, 0.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    /** Оновлює значення властивості. */
    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress,
                          float headYaw, float headPitch) {
        this.head.yaw = headYaw * ((float) Math.PI / 180F);
        this.head.pitch = headPitch * ((float) Math.PI / 180F);

        this.rightArm.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 2.0F * limbDistance * 0.5F;
        this.leftArm.pitch = MathHelper.cos(limbAngle * 0.6662F) * 2.0F * limbDistance * 0.5F;
        this.rightLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
        this.leftLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
    }

    /** Повертає поточне значення властивості. */
    @Override
    public ModelPart getHead() {
        return this.head;
    }

    /** Повертає поточне значення властивості. */
    @Override
    public ModelPart getPart() {
        return this.root;
    }

    /** Рендерить сутність. */
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay,
                       float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
