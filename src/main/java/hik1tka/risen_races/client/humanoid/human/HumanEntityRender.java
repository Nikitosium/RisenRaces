package hik1tka.risen_races.client.humanoid.human;

import hik1tka.risen_races.client.humanoid.human.model.profession.hat.FarmerHatModel;
import hik1tka.risen_races.client.humanoid.human.model.profession.hat.FishermanHatModel;
import hik1tka.risen_races.entity.humanoid.human.HumanEntity;
import hik1tka.risen_races.register.ModModelLayers;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class HumanEntityRender extends MobEntityRenderer<HumanEntity, PlayerEntityModel<HumanEntity>> {
    private final PlayerEntityModel<HumanEntity> wideModel;
    private final PlayerEntityModel<HumanEntity> slimModel;

    public HumanEntityRender(EntityRendererFactory.Context context) {
        super(context, createModel(context.getPart(EntityModelLayers.PLAYER), false), 0.5f);
        this.wideModel = this.model;
        this.slimModel = createModel(context.getPart(EntityModelLayers.PLAYER_SLIM), true);

        this.addFeature(new NPCClothingFeatureRenderer(this));

        FarmerHatModel<HumanEntity> farmerHat = new FarmerHatModel<>(context.getPart(ModModelLayers.FARMER_HAT));
        this.addFeature(new FarmerHatFeatureRenderer<>(this, farmerHat));

        FishermanHatModel<HumanEntity> fishermanHat = new FishermanHatModel<>(context.getPart(ModModelLayers.FISHERMAN_HAT));
        this.addFeature(new FishermanHatFeatureRenderer<>(this, fishermanHat));
    }









    private static PlayerEntityModel<HumanEntity> createModel(ModelPart root, boolean slim) {
        return new PlayerEntityModel<>(root, slim) {
            /** Оновлює значення властивості. */
            @Override
            public void setAngles(HumanEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
                super.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
                this.child = false;
            }
        };
    }







    /** Рендерить сутність. */
    @Override
    public void render(HumanEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {
        this.model = entity.isFemale() ? this.slimModel : this.wideModel;
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    /** Повертає текстуру сутності. */
    @Override
    public Identifier getTexture(HumanEntity entity) {
        int id = entity.getSkinId();
        String path = entity.getHumanoidData().getProfession().equals("none")
                ? "textures/entity/human/nitwit_skins/"
                : "textures/entity/human/work_skins/";

        if (entity.isFemale()) {
            switch (id) {
                case 0: return new Identifier("risen_races", path + "adele.png");
                case 1: return new Identifier("risen_races", path + "beatrice.png");
                case 2: return new Identifier("risen_races", path + "clara.png");
                case 3: return new Identifier("risen_races", path + "elsa.png");
                case 4: return new Identifier("risen_races", path + "ingrid.png");
                case 5: return new Identifier("risen_races", path + "freya.png");
                case 6: return new Identifier("risen_races", path + "hilda.png");
                case 7: return new Identifier("risen_races", path + "martha.png");
                case 8: return new Identifier("risen_races", path + "olivia.png");
                case 9: return new Identifier("risen_races", path + "rosalia.png");
                case 10: return new Identifier("risen_races", path + "sigrid.png");
                case 11: return new Identifier("risen_races", path + "valeria.png");
                default: return new Identifier("risen_races", path + "valeria.png");
            }
        } else {
            switch (id) {
                case 0: return new Identifier("risen_races", path + "albert.png");
                case 1: return new Identifier("risen_races", path + "bruno.png");
                case 2: return new Identifier("risen_races", path + "derek.png");
                case 3: return new Identifier("risen_races", path + "erik.png");
                case 4: return new Identifier("risen_races", path + "gunnar.png");
                case 5: return new Identifier("risen_races", path + "konrad.png");
                case 6: return new Identifier("risen_races", path + "marcus.png");
                case 7: return new Identifier("risen_races", path + "olaf.png");
                case 8: return new Identifier("risen_races", path + "roland.png");
                case 9: return new Identifier("risen_races", path + "victor.png");
                default: return new Identifier("risen_races", path + "victor.png");
            }
        }
    }

    /** Застосовує масштаб моделі. */
    @Override
    protected void scale(HumanEntity entity, MatrixStack matrices, float amount) {
        float f = entity.getScaleFactor();
        matrices.scale(f, f, f);
    }
}
