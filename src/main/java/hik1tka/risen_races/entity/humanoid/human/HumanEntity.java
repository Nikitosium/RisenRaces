package hik1tka.risen_races.entity.humanoid.human;

import hik1tka.risen_races.RisenRaces;
import hik1tka.risen_races.entity.humanoid.HumanoidEntity;
import hik1tka.risen_races.entity.humanoid.data.ProfessionDefinition;
import hik1tka.risen_races.util.IGenderedEntity;
import hik1tka.risen_races.register.ModSounds;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HumanEntity extends HumanoidEntity implements IGenderedEntity {
    public static final EntityType<HumanEntity> HUMAN = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(RisenRaces.MOD_ID, "human"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, HumanEntity::new)
                    .dimensions(EntityDimensions.changing(0.6f, 1.8f))
                    .build()
    );

    /** Створює потрібний обєкт або сутність. */
    public static net.minecraft.entity.attribute.DefaultAttributeContainer.Builder createHumanAttributes() {

        return HumanoidEntity.createHumanoidAttributes();
    }

    private static final TrackedData<Integer> SKIN_ID = DataTracker.registerData(HumanEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public HumanEntity(EntityType<? extends HumanoidEntity> entityType, World world) {
        super(entityType, world);
    }

    /** Реєструє синхронізовані дані сутності. */
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SKIN_ID, 0);
    }

    /** Виконує дію компонента. */
    @Override
    protected void afterUsing(TradeOffer offer) {

    }

    /** Ініціалізує стан сутності під час спавну. */
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setRace(hik1tka.risen_races.entity.humanoid.HumanoidRace.HUMAN);

        this.setFemale(this.random.nextBoolean());
        this.rollSkinForGender();

        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    private void rollSkinForGender() {
        if (this.isFemale()) {

            this.setSkinId(this.random.nextInt(12));
        } else {

            this.setSkinId(this.random.nextInt(10));
        }
    }

    /** Обробляє подію життєвого циклу. */
    @Override
    protected void onBabyCreated(hik1tka.risen_races.entity.humanoid.HumanoidEntity baby) {


        if (baby instanceof HumanEntity human) {
            human.rollSkinForGender();
        }
    }

    /** Створює потрібний обєкт або сутність. */
    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    /** Повертає поточне значення властивості. */
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isFemale() ? ModSounds.FEMALE_AMBIENT : ModSounds.MALE_AMBIENT;
    }

    /** Повертає поточне значення властивості. */
    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        if (!this.isFemale() && this.random.nextInt(1000) == 0) {
            return ModSounds.MALE_NO_GOD;
        }
        return this.isFemale() ? ModSounds.FEMALE_HURT : ModSounds.MALE_HURT;
    }

    /** Повертає поточне значення властивості. */
    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ENTITY_DEATH;
    }

    /** Повертає поточне значення властивості. */
    @Override
    protected SoundEvent getTradingSound(boolean sold) {
        if (!sold) {
            return this.isFemale() ? super.getTradingSound(false) : ModSounds.MALE_NO;


        }
        return super.getTradingSound(sold);
    }

    /** Повертає поточне значення властивості. */
    public int getSkinId() {
        return this.dataTracker.get(SKIN_ID);
    }

    /** Повертає поточне значення властивості. */
    @Override
    public float getScaleFactor() {
        return this.isBaby() ? 0.7f : 1.0f;
    }

    /** Повертає габарити хітбоксу. */
    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return super.getDimensions(pose).scaled(getScaleFactor());
    }

    /** Оновлює значення властивості. */
    public void setSkinId(int id) {
        this.dataTracker.set(SKIN_ID, id);
    }

    /** Обробляє отриману шкоду. */
    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.getWorld().isClient
                && amount >= this.getHealth()
                && source.getAttacker() instanceof net.minecraft.entity.mob.ZombieEntity
                && this.getWorld() instanceof net.minecraft.server.world.ServerWorld serverWorld) {

            float chance = getZombificationChance(serverWorld.getDifficulty());
            if (this.random.nextFloat() < chance) {
                tryZombify(serverWorld);
                return true;
            }

        }
        return super.damage(source, amount);
    }

    private static float getZombificationChance(net.minecraft.world.Difficulty difficulty) {
        return switch (difficulty) {
            case PEACEFUL, EASY -> 0.0f;
            case NORMAL -> 0.5f;
            case HARD -> 1.0f;
        };
    }

    private void tryZombify(net.minecraft.server.world.ServerWorld world) {
        hik1tka.risen_races.util.ZombieVariant variant =
                hik1tka.risen_races.util.ZombieVariantHelper.resolveVariant(world, this);



        net.minecraft.entity.mob.ZombieEntity zombie =
                hik1tka.risen_races.util.ZombieVariantHelper.create(world, variant);
        if (zombie == null) return;

        zombie.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());

        zombie.setBaby(this.isBaby());

        net.minecraft.nbt.NbtCompound memory = new net.minecraft.nbt.NbtCompound();
        memory.putBoolean("WasFemale", this.isFemale());
        memory.putInt("SkinID", this.getSkinId());
        memory.putString("Profession", this.getProfession());
        memory.putBoolean("IsBaby", this.isBaby());
        memory.putString("StoredName", this.hasCustomName() ? this.getCustomName().getString() : "");

        if (zombie instanceof hik1tka.risen_races.entity.zombie.IZombifiedHuman carrier) {
            carrier.setFemale(this.isFemale());
            carrier.setProfession(this.getProfession());
            carrier.setNpcMemory(memory);
        }

        world.spawnEntity(zombie);
        this.discard();
    }

    /** Повертає поточне значення властивості. */
    @Override
    public java.util.List<hik1tka.risen_races.entity.humanoid.data.ProfessionDefinition> getAvailableProfessions() {
        return java.util.List.of(
                new ProfessionDefinition("farmer", net.minecraft.world.poi.PointOfInterestTypes.FARMER, 48),
                new ProfessionDefinition("butcher", net.minecraft.world.poi.PointOfInterestTypes.BUTCHER, 48),
                new ProfessionDefinition("shepherd", net.minecraft.world.poi.PointOfInterestTypes.SHEPHERD, 48),
                new ProfessionDefinition("fisherman", net.minecraft.world.poi.PointOfInterestTypes.FISHERMAN, 48),
                new ProfessionDefinition("leatherworker", net.minecraft.world.poi.PointOfInterestTypes.LEATHERWORKER, 48),
                new ProfessionDefinition("cleric", net.minecraft.world.poi.PointOfInterestTypes.CLERIC, 48),
                new ProfessionDefinition("cartographer", net.minecraft.world.poi.PointOfInterestTypes.CARTOGRAPHER, 48),

                new hik1tka.risen_races.entity.humanoid.data.ProfessionDefinition(
                        "builder", net.minecraft.world.poi.PointOfInterestTypes.MASON, 48)
        );
    }

    /** Повертає поточне значення властивості. */
    public float getScaleModifier() {
        return this.isFemale() ? 0.95f : 1.0f;
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
        nbt.putInt("SkinId", this.getSkinId());
    }

    /** Відновлює стан з NBT. */
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("SkinId")) {
            this.setSkinId(nbt.getInt("SkinId"));
        }
    }
}
