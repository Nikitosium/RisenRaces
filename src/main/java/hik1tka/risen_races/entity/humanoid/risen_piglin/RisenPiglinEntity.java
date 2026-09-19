package hik1tka.risen_races.entity.humanoid.risen_piglin;

import hik1tka.risen_races.RisenRaces;
import hik1tka.risen_races.entity.humanoid.HumanoidEntity;
import hik1tka.risen_races.entity.humanoid.HumanoidRace;
import hik1tka.risen_races.entity.humanoid.data.ProfessionDefinition;
import hik1tka.risen_races.entity.humanoid.goal.FollowRescuerGoal;
import hik1tka.risen_races.entity.humanoid.goal.LowHealthFleeGoal;
import hik1tka.risen_races.entity.humanoid.goal.SeekNetherPortalGoal;
import hik1tka.risen_races.util.IGenderedEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class RisenPiglinEntity extends HumanoidEntity implements IGenderedEntity {

    public static final EntityType<RisenPiglinEntity> RISEN_PIGLIN = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(RisenRaces.MOD_ID, "risen_piglin"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, RisenPiglinEntity::new)
                    .dimensions(EntityDimensions.changing(0.6f, 1.95f))
                    .build()
    );

    /** Створює потрібний обєкт або сутність. */
    public static net.minecraft.entity.attribute.DefaultAttributeContainer.Builder createRisenPiglinAttributes() {
        return HumanoidEntity.createHumanoidAttributes();
    }

    private static final float LOW_HEALTH_THRESHOLD = 5.0f;

    private static final double TRAIN_SEARCH_RADIUS = 16.0D;




    @Nullable
    private UUID rescuerUuid;




    @Nullable
    private UUID trainLeaderUuid;

    public RisenPiglinEntity(EntityType<? extends HumanoidEntity> entityType, World world) {
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
        this.setRace(HumanoidRace.RIZEN_PIGLIN);
        this.setFemale(this.random.nextBoolean());
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    /** Налаштовує цілі поведінки. */
    @Override
    protected void initGoals() {
        super.initGoals();



        this.goalSelector.add(0, new LowHealthFleeGoal(this));


        this.goalSelector.add(1, new SeekNetherPortalGoal(this));



        this.goalSelector.add(4, new FollowRescuerGoal(this));
    }








    /** Оновлює значення властивості. */
    public void setRescuer(PlayerEntity player) {
        this.rescuerUuid = player.getUuid();
        assignTrainPosition();
    }

    /** Перевіряє поточну умову. */
    public boolean hasRescuer() {
        return this.rescuerUuid != null;
    }

    /** Повертає поточне значення властивості. */
    @Nullable
    public UUID getRescuerUuid() {
        return this.rescuerUuid;
    }

    /** Повертає поточне значення властивості. */
    @Override
    public float getSoundPitch() {
        float base = super.getSoundPitch();
        if (this.isBaby()) return base;
        return this.isFemale() ? base * 1.15f : base * 0.9f;
    }








    private void assignTrainPosition() {
        if (!(this.getWorld() instanceof ServerWorld serverWorld) || rescuerUuid == null) {
            return;
        }

        Box box = this.getBoundingBox().expand(TRAIN_SEARCH_RADIUS);
        List<RisenPiglinEntity> siblings = serverWorld.getEntitiesByClass(
                RisenPiglinEntity.class, box,
                e -> e != this && rescuerUuid.equals(e.rescuerUuid));

        if (siblings.isEmpty()) {
            this.trainLeaderUuid = null;
            return;
        }



        Set<UUID> alreadyFollowed = new HashSet<>();
        for (RisenPiglinEntity sibling : siblings) {
            if (sibling.trainLeaderUuid != null) {
                alreadyFollowed.add(sibling.trainLeaderUuid);
            }
        }

        RisenPiglinEntity tail = null;
        double closestDistSq = Double.MAX_VALUE;
        for (RisenPiglinEntity sibling : siblings) {
            if (alreadyFollowed.contains(sibling.getUuid())) {
                continue;
            }
            double d = this.squaredDistanceTo(sibling);
            if (d < closestDistSq) {
                closestDistSq = d;
                tail = sibling;
            }
        }

        this.trainLeaderUuid = (tail != null) ? tail.getUuid() : null;
    }








    /** Знаходить потрібний обєкт. */
    @Nullable
    public LivingEntity resolveFollowTarget() {
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) {
            return null;
        }

        if (trainLeaderUuid != null) {
            Entity leader = serverWorld.getEntity(trainLeaderUuid);
            if (leader instanceof RisenPiglinEntity piglinLeader && piglinLeader.isAlive()) {
                return piglinLeader;
            }
            trainLeaderUuid = null;
        }

        if (rescuerUuid != null) {
            PlayerEntity player = serverWorld.getPlayerByUuid(rescuerUuid);
            if (player != null && player.isAlive()) {
                return player;
            }
        }
        return null;
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
        if (!this.isBaby()) {
            return 1.0f;
        }
        return this.isFemale() ? 0.65f : 0.7f;
    }

    /** Повертає поточне значення властивості. */
    @Override
    public float getHatYOffset() {



        return -0.125F;
    }

    /** Обробляє отриману шкоду. */
    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean hurt = super.damage(source, amount);
        if (hurt && !this.getWorld().isClient && source.getAttacker() instanceof LivingEntity attacker) {
            reactToDamage(attacker);
        }
        return hurt;
    }

    private void reactToDamage(LivingEntity attacker) {
        if (this.getHealth() < LOW_HEALTH_THRESHOLD) {

            this.setTarget(null);
            return;
        }


        if (this.hasRescuer() && attacker instanceof PlayerEntity player
                && player.getUuid().equals(this.rescuerUuid)) {
            return;
        }
        this.setTarget(attacker);
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
        if (this.rescuerUuid != null) {
            nbt.putUuid("RescuerUuid", this.rescuerUuid);
        }
        if (this.trainLeaderUuid != null) {
            nbt.putUuid("TrainLeaderUuid", this.trainLeaderUuid);
        }
    }

    /** Відновлює стан з NBT. */
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.rescuerUuid = nbt.containsUuid("RescuerUuid") ? nbt.getUuid("RescuerUuid") : null;
        this.trainLeaderUuid = nbt.containsUuid("TrainLeaderUuid") ? nbt.getUuid("TrainLeaderUuid") : null;
    }
}
