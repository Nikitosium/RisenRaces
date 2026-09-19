package hik1tka.risen_races.entity.humanoid.rynar;

import hik1tka.risen_races.RisenRaces;
import hik1tka.risen_races.entity.humanoid.HumanoidEntity;
import hik1tka.risen_races.entity.humanoid.HumanoidRace;
import hik1tka.risen_races.entity.humanoid.data.ProfessionDefinition;
import hik1tka.risen_races.util.IGenderedEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;






public class RynarEntity extends HumanoidEntity implements IGenderedEntity {

    public static final EntityType<RynarEntity> RYNAR = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(RisenRaces.MOD_ID, "rynar"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, RynarEntity::new)
                    .dimensions(EntityDimensions.changing(0.6f, 1.8f))
                    .build()
    );

    /** Створює потрібний обєкт або сутність. */
    public static net.minecraft.entity.attribute.DefaultAttributeContainer.Builder createRynarAttributes() {
        return HumanoidEntity.createHumanoidAttributes();
    }

    public RynarEntity(EntityType<? extends HumanoidEntity> entityType, World world) {
        super(entityType, world);
    }

    /** Виконує дію компонента. */
    @Override
    protected void afterUsing(TradeOffer offer) {

    }

    /** Ініціалізує стан сутності під час спавну. */
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
                                 @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setRace(HumanoidRace.RYNAR);



        this.setFemale(this.random.nextBoolean());
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    /** Обробляє подію життєвого циклу. */
    @Override
    protected void onBabyCreated(HumanoidEntity baby) {


    }

    /** Створює потрібний обєкт або сутність. */
    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    /** Повертає поточне значення властивості. */
    public float getScaleFactor() {
        return this.isBaby() ? 0.7f : 1.0f;
    }

    /** Повертає поточне значення властивості. */
    @Override
    public float getSoundPitch() {
        float base = super.getSoundPitch();
        if (this.isBaby()) return base;
        return this.isFemale() ? base * 1.15f : base * 0.9f;
    }

    /** Повертає поточне значення властивості. */
    @Override
    public List<ProfessionDefinition> getAvailableProfessions() {
        return List.of(
                new ProfessionDefinition("farmer", net.minecraft.world.poi.PointOfInterestTypes.FARMER, 48),
                new ProfessionDefinition("butcher", net.minecraft.world.poi.PointOfInterestTypes.BUTCHER, 48),
                new ProfessionDefinition("shepherd", net.minecraft.world.poi.PointOfInterestTypes.SHEPHERD, 48),
                new ProfessionDefinition("fisherman", net.minecraft.world.poi.PointOfInterestTypes.FISHERMAN, 48),
                new ProfessionDefinition("leatherworker", net.minecraft.world.poi.PointOfInterestTypes.LEATHERWORKER, 48),
                new ProfessionDefinition("cleric", net.minecraft.world.poi.PointOfInterestTypes.CLERIC, 48),
                new ProfessionDefinition("cartographer", net.minecraft.world.poi.PointOfInterestTypes.CARTOGRAPHER, 48)
        );
    }

    /** Повертає поточне значення властивості. */
    @Override
    public String getRaceId() {
        return this.getRace().name().toLowerCase(java.util.Locale.ROOT);
    }

    /** Перевіряє поточну умову. */
    @Override
    public boolean isInLove() {
        return false;
    }

    /** Оновлює значення властивості. */
    @Override
    public void setLoveTicks(int ticks) {
    }

    /** Перевіряє поточну умову. */
    @Override
    public boolean canBreedWith(PassiveEntity other) {
        return this.canBreedWithGendered(other);
    }

    /** Зберігає стан у NBT. */
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

    }

    /** Відновлює стан з NBT. */
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
    }
}
