package hik1tka.risen_races.entity.zombie;

import hik1tka.risen_races.RisenRaces;
import hik1tka.risen_races.entity.humanoid.HumanoidEntity;
import hik1tka.risen_races.entity.humanoid.human.HumanEntity;
import hik1tka.risen_races.util.NPCConstants;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


















public class ZombifiedHumanDrownedEntity extends DrownedEntity implements IZombifiedHuman {

    public static final EntityType<ZombifiedHumanDrownedEntity> ZOMBIFIED_HUMAN_DROWNED = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(RisenRaces.MOD_ID, "zombified_human_drowned"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ZombifiedHumanDrownedEntity::new)
                    .dimensions(EntityDimensions.changing(0.6f, 1.95f))
                    .build()
    );





    private static final TrackedData<Boolean> IS_FEMALE =
            DataTracker.registerData(ZombifiedHumanDrownedEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<String> PROFESSION =
            DataTracker.registerData(ZombifiedHumanDrownedEntity.class, TrackedDataHandlerRegistry.STRING);




    private static final String[] PROFESSION_POOL = {
            "farmer", "butcher", "shepherd", "fisherman", "leatherworker", "cleric", "cartographer"
    };
    private static final float UNEMPLOYED_CHANCE = 0.4f;


    private static final float ASSUMED_MAX_DIFFICULTY = 6.75f;
    private static final float CURE_CHANCE_AT_MIN_DIFFICULTY = 0.35f;
    private static final float CURE_CHANCE_AT_MAX_DIFFICULTY = 0.10f;

    @Nullable
    private NbtCompound npcMemory;












    /** Створює потрібний обєкт або сутність. */
    public static DefaultAttributeContainer.Builder createZombifiedHumanDrownedAttributes() {
        return ZombieEntity.createZombieAttributes()
                .add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS, 0.0);
    }

    /** Повертає поточне значення властивості. */
    @Override
    public Identifier getLootTableId() {
        return EntityType.DROWNED.getLootTableId();
    }

    public ZombifiedHumanDrownedEntity(EntityType<? extends DrownedEntity> entityType, World world) {
        super(entityType, world);
    }








    /** Ініціалізує стан сутності під час спавну. */
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
                                 @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.rollRandomSpawnData();
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    /** Реєструє синхронізовані дані сутності. */
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(IS_FEMALE, false);
        this.dataTracker.startTracking(PROFESSION, "none");
    }



    /** Перевіряє поточну умову. */
    @Override
    public boolean isFemale() {
        return this.dataTracker.get(IS_FEMALE);
    }

    /** Оновлює значення властивості. */
    @Override
    public void setFemale(boolean female) {
        this.dataTracker.set(IS_FEMALE, female);
    }

    /** Повертає поточне значення властивості. */
    @Override
    public String getProfession() {
        return this.dataTracker.get(PROFESSION);
    }

    /** Оновлює значення властивості. */
    @Override
    public void setProfession(String profession) {
        this.dataTracker.set(PROFESSION, profession);
    }

    /** Повертає поточне значення властивості. */
    @Nullable
    @Override
    public NbtCompound getNpcMemory() {
        return this.npcMemory;
    }

    /** Оновлює значення властивості. */
    @Override
    public void setNpcMemory(@Nullable NbtCompound memory) {
        this.npcMemory = memory;
    }

    /** Генерує випадковий стан. */
    @Override
    public void rollRandomSpawnData() {
        setFemale(this.random.nextBoolean());
        setProfession(this.random.nextFloat() < UNEMPLOYED_CHANCE
                ? "none"
                : PROFESSION_POOL[this.random.nextInt(PROFESSION_POOL.length)]);
    }



    /** Зберігає стан у NBT. */
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("RisenIsFemale", isFemale());
        nbt.putString("RisenProfession", getProfession());
        if (npcMemory != null) {
            nbt.put("MD_NPC_Memory", npcMemory);
        }
    }

    /** Відновлює стан з NBT. */
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("RisenIsFemale")) setFemale(nbt.getBoolean("RisenIsFemale"));
        if (nbt.contains("RisenProfession")) setProfession(nbt.getString("RisenProfession"));
        if (nbt.contains("MD_NPC_Memory")) npcMemory = nbt.getCompound("MD_NPC_Memory");
    }



    /** Повертає поточне значення властивості. */
    @Override
    public float getSoundPitch() {


        if (this.isBaby()) {
            return (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F;
        }
        float base = super.getSoundPitch();
        return isFemale() ? base * 1.15f : base * 0.9f;
    }



    /** Повертає поточне значення властивості. */
    public float getCureChance(ServerWorld world) {
        float localDifficulty = world.getLocalDifficulty(this.getBlockPos()).getClampedLocalDifficulty();
        float t = MathHelper.clamp(localDifficulty / ASSUMED_MAX_DIFFICULTY, 0.0f, 1.0f);
        return MathHelper.lerp(t, CURE_CHANCE_AT_MIN_DIFFICULTY, CURE_CHANCE_AT_MAX_DIFFICULTY);
    }





    /** Виконує спробу дії. */
    public boolean tryCure(ServerWorld world) {
        if (world.getRandom().nextFloat() >= getCureChance(world)) {
            return false;
        }

        HumanoidEntity restored = createRestoredHuman(world);
        if (restored == null) return false;

        restored.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
        world.spawnEntity(restored);
        this.discard();
        return true;
    }





    /** Створює потрібний обєкт або сутність. */
    @Nullable
    protected HumanoidEntity createRestoredHuman(ServerWorld world) {
        HumanEntity human = HumanEntity.HUMAN.create(world);
        if (human == null) return null;

        Random random = world.getRandom();
        if (npcMemory != null) {
            human.setFemale(npcMemory.getBoolean("WasFemale"));
            human.setSkinId(npcMemory.getInt("SkinID"));
            human.setProfession(npcMemory.contains("Profession") ? npcMemory.getString("Profession") : "none");
            human.setBreedingAge(npcMemory.getBoolean("IsBaby") ? -24000 : 0);
            String storedName = npcMemory.getString("StoredName");
            if (!storedName.isEmpty()) {
                human.setCustomName(Text.literal(storedName));
                human.setCustomNameVisible(true);
            }
        } else {
            boolean female = isFemale();
            human.setFemale(female);
            human.setSkinId(female ? random.nextInt(12) : random.nextInt(10));
            human.setProfession("none");
            String[] pool = female ? NPCConstants.FEMALE_NAMES : NPCConstants.MALE_NAMES;
            human.setCustomName(Text.literal(pool[random.nextInt(pool.length)]));
            human.setCustomNameVisible(true);
        }
        return human;
    }
}
