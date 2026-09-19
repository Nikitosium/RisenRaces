package hik1tka.risen_races.entity.humanoid;

import hik1tka.risen_races.entity.humanoid.data.HumanoidData;
import hik1tka.risen_races.entity.humanoid.data.ProfessionDefinition;
import hik1tka.risen_races.entity.humanoid.goal.AcquireProfessionGoal;
import hik1tka.risen_races.entity.humanoid.goal.ConditionalGoal;
import hik1tka.risen_races.entity.humanoid.goal.FindMateGoal;
import hik1tka.risen_races.entity.humanoid.goal.PanicUntilSafeGoal;
import hik1tka.risen_races.entity.humanoid.goal.PickUpFoodGoal;
import hik1tka.risen_races.entity.humanoid.goal.RizenPiglinDefenseGoal;
import hik1tka.risen_races.util.VillageCapacityHelper;
import net.minecraft.entity.EntityType;

import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.MerchantEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;

import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Map;

public abstract class HumanoidEntity extends MerchantEntity {

    private static final TrackedData<Integer> RACE =
            DataTracker.registerData(HumanoidEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_FEMALE =
            DataTracker.registerData(HumanoidEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<String> PROFESSION =
            DataTracker.registerData(HumanoidEntity.class, TrackedDataHandlerRegistry.STRING);

    public static final int INVENTORY_SIZE = 8;
    private final SimpleInventory inventory = new SimpleInventory(INVENTORY_SIZE);

    private record FoodInfo(int points, int babies) {}

    private static final Map<Item, FoodInfo> BREEDING_FOOD_VALUES = Map.ofEntries(
            Map.entry(Items.BREAD, new FoodInfo(4, 1)),
            Map.entry(Items.POTATO, new FoodInfo(1, 1)),
            Map.entry(Items.CARROT, new FoodInfo(1, 1)),
            Map.entry(Items.BEETROOT, new FoodInfo(1, 1)),
            Map.entry(Items.CHICKEN, new FoodInfo(6, 1)),
            Map.entry(Items.COOKED_CHICKEN, new FoodInfo(12, 1)),
            Map.entry(Items.BEEF, new FoodInfo(12, 1)),
            Map.entry(Items.COOKED_BEEF, new FoodInfo(24, 2)),
            Map.entry(Items.PORKCHOP, new FoodInfo(12, 1)),
            Map.entry(Items.COOKED_PORKCHOP, new FoodInfo(24, 2)),
            Map.entry(Items.PUMPKIN_PIE, new FoodInfo(12, 1)),
            Map.entry(Items.GOLDEN_CARROT, new FoodInfo(32, 3)),
            Map.entry(Items.GOLDEN_APPLE, new FoodInfo(32, 3)),
            Map.entry(Items.ENCHANTED_GOLDEN_APPLE, new FoodInfo(48, 4))
    );

    /** Перевіряє поточну умову. */
    public static boolean isBreedingFood(Item item) {
        return BREEDING_FOOD_VALUES.containsKey(item);
    }

    public static final int BREEDING_FOOD_REQUIREMENT = 12;

    @Nullable
    private BlockPos jobSite;

    private int breedingCooldown = 0;

    private int pendingBabies = 0;

    private long nextPendingBirthTick = -1L;

    public HumanoidEntity(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    /** Створює потрібний обєкт або сутність. */
    public static DefaultAttributeContainer.Builder createHumanoidAttributes() {
        return MerchantEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.27D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0D)




                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0D);
    }

    /** Реєструє синхронізовані дані сутності. */
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(RACE, HumanoidRace.HUMAN.ordinal());


        this.dataTracker.startTracking(IS_FEMALE, false);
        this.dataTracker.startTracking(PROFESSION, "none");
    }

    /** Повертає поточне значення властивості. */
    public HumanoidData getHumanoidData() {
        return new HumanoidData(
                this.getRace().name().toLowerCase(java.util.Locale.ROOT),
                this.isFemale(),
                this.getProfession(),
                1
        );
    }

    /** Повертає поточне значення властивості. */
    public String getProfession() {
        return this.dataTracker.get(PROFESSION);
    }

    /** Оновлює значення властивості. */
    public void setProfession(String professionId) {
        this.dataTracker.set(PROFESSION, professionId);
    }

    /** Повертає поточне значення властивості. */
    @Nullable
    public BlockPos getJobSite() {
        return jobSite;
    }

    /** Оновлює значення властивості. */
    public void setJobSite(BlockPos pos) {
        this.jobSite = pos;
    }

    /** Виконує дію компонента. */
    public void clearJobSite() {
        this.jobSite = null;
        setProfession("none");
    }

    /** Повертає поточне значення властивості. */
    public List<ProfessionDefinition> getAvailableProfessions() {
        return List.of();
    }

    /** Повертає поточне значення властивості. */
    public SimpleInventory getInventory() {
        return this.inventory;
    }

    /** Перевіряє поточну умову. */
    @Override
    public boolean canPickUpLoot() {
        return true;
    }

    /** Перевіряє поточну умову. */
    @Override
    public boolean canGather(ItemStack stack) {


        if (!isBreedingFood(stack.getItem())) {
            return false;
        }
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack slot = inventory.getStack(i);
            if (slot.isEmpty()) {
                return true;
            }
            if (ItemStack.canCombine(slot, stack) && slot.getCount() < slot.getMaxCount()) {
                return true;
            }
        }
        return false;
    }

    /** Виконує дію компонента. */
    @Override
    protected void loot(net.minecraft.entity.ItemEntity item) {

        ItemStack stack = item.getStack();
        if (!canGather(stack)) {
            return;
        }
        int pickedUp = 0;
        for (int i = 0; i < inventory.size() && !stack.isEmpty(); i++) {
            ItemStack slot = inventory.getStack(i);
            if (slot.isEmpty()) {
                int amount = stack.getCount();
                inventory.setStack(i, stack.split(amount));
                pickedUp += amount;
            } else if (ItemStack.canCombine(slot, stack)) {
                int space = slot.getMaxCount() - slot.getCount();
                int amount = Math.min(space, stack.getCount());
                if (amount > 0) {
                    slot.increment(amount);
                    stack.decrement(amount);
                    pickedUp += amount;
                }
            }
        }
        if (pickedUp > 0) {
            this.sendPickup(item, pickedUp);
        }
        if (stack.isEmpty()) {
            item.discard();
        } else {
            item.setStack(stack);
        }
    }

    /** Повертає поточне значення властивості. */
    public int getFoodValueInInventory() {
        int total = 0;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            FoodInfo info = BREEDING_FOOD_VALUES.get(stack.getItem());
            if (info != null) {
                total += info.points() * stack.getCount();
            }
        }
        return total;
    }

    /** Перевіряє поточну умову. */
    public boolean hasEnoughFoodToBreed() {
        return getFoodValueInInventory() >= BREEDING_FOOD_REQUIREMENT;
    }

    /** Повертає поточне значення властивості. */
    public int getTotalFoodItemCount() {
        int total = 0;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (isBreedingFood(stack.getItem())) {
                total += stack.getCount();
            }
        }
        return total;
    }

    /** Виконує дію компонента. */
    public ItemStack receiveFoodGift(ItemStack stack) {
        for (int i = 0; i < inventory.size() && !stack.isEmpty(); i++) {
            ItemStack slot = inventory.getStack(i);
            if (slot.isEmpty()) {
                inventory.setStack(i, stack.split(stack.getCount()));
            } else if (ItemStack.canCombine(slot, stack)) {
                int space = slot.getMaxCount() - slot.getCount();
                int amount = Math.min(space, stack.getCount());
                if (amount > 0) {
                    slot.increment(amount);
                    stack.decrement(amount);
                }
            }
        }
        return stack;
    }

    /** Виконує дію компонента. */
    public boolean shareOneFoodItemWith(HumanoidEntity other) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (isBreedingFood(stack.getItem()) && stack.getCount() >= 2) {
                ItemStack gift = stack.copy();
                gift.setCount(1);
                ItemStack leftover = other.receiveFoodGift(gift);
                if (leftover.isEmpty()) {
                    stack.decrement(1);
                    return true;
                }
            }
        }
        return false;
    }

    private int consumeBreedingFoodAndGetBabies() {
        int remaining = BREEDING_FOOD_REQUIREMENT;
        int bestBabies = 1;
        for (int i = 0; i < inventory.size() && remaining > 0; i++) {
            ItemStack stack = inventory.getStack(i);
            FoodInfo info = BREEDING_FOOD_VALUES.get(stack.getItem());
            if (info == null || stack.isEmpty()) {
                continue;
            }
            while (remaining > 0 && !stack.isEmpty()) {
                stack.decrement(1);
                remaining -= info.points();
                bestBabies = Math.max(bestBabies, info.babies());
            }
        }
        return bestBabies;
    }

    /** Повертає поточне значення властивості. */
    public float getHatYOffset() {
        return 0.0F;
    }

    /** Повертає поточне значення властивості. */
    public HumanoidRace getRace() {
        return HumanoidRace.values()[this.dataTracker.get(RACE)];
    }

    /** Оновлює значення властивості. */
    public void setRace(HumanoidRace race) {
        this.dataTracker.set(RACE, race.ordinal());
    }

    /** Перевіряє поточну умову. */
    public boolean isFemale() {
        return this.dataTracker.get(IS_FEMALE);
    }

    /** Оновлює значення властивості. */
    public void setFemale(boolean female) {
        this.dataTracker.set(IS_FEMALE, female);
    }

    /** Перевіряє поточну умову. */
    public boolean isBreedingReady() {
        return breedingCooldown <= 0;
    }

    /** Повертає поточне значення властивості. */
    @Override
    public float getSoundPitch() {
        if (this.isBaby()) {
            return (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F;
        }
        return super.getSoundPitch();
    }

    /** Виконує дію компонента. */
    public void resetBreedingCooldown() {

        this.breedingCooldown = 6000;
    }

    /** Перевіряє поточну умову. */
    public boolean canBreedWith(HumanoidEntity other) {
        if (other == this) return false;
        if (this.getRace() != other.getRace()) return false;
        if (this.isFemale() == other.isFemale()) return false;
        if (!this.isBreedingReady() || !other.isBreedingReady()) return false;
        return this.hasEnoughFoodToBreed() && other.hasEnoughFoodToBreed();
    }

    /** Виконує дію компонента. */
    public void breedWith(HumanoidEntity partner) {
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) return;
        if (!canBreedWith(partner)) return;

        int babiesFromThis = this.consumeBreedingFoodAndGetBabies();
        int babiesFromPartner = partner.consumeBreedingFoodAndGetBabies();
        int requestedBabies = Math.max(babiesFromThis, babiesFromPartner);

        int babyCount = VillageCapacityHelper.capBabyCount(this, requestedBabies);

        for (int i = 0; i < babyCount; i++) {
            spawnOneBaby(serverWorld);
        }

        this.resetBreedingCooldown();
        partner.resetBreedingCooldown();

        int shortfall = requestedBabies - babyCount;
        if (shortfall > 0) {



            queuePendingBabies(shortfall);




            VillageCapacityHelper.announceQueuedBirths(this, shortfall);
        } else {



            VillageCapacityHelper.announceIfFull(this);
        }
    }

    private void spawnOneBaby(ServerWorld serverWorld) {
        HumanoidEntity baby = (HumanoidEntity) getType().create(serverWorld);
        if (baby == null) return;

        baby.setRace(this.getRace());
        baby.setFemale(this.random.nextBoolean());

        baby.setBreedingAge(-24000);

        double offsetX = (this.random.nextDouble() - 0.5D) * 1.5D;
        double offsetZ = (this.random.nextDouble() - 0.5D) * 1.5D;
        baby.refreshPositionAndAngles(this.getX() + offsetX, this.getY(), this.getZ() + offsetZ, 0.0F, 0.0F);

        onBabyCreated(baby);
        serverWorld.spawnEntityAndPassengers(baby);
    }

    private void queuePendingBabies(int count) {
        boolean wasEmpty = this.pendingBabies <= 0;
        this.pendingBabies += count;
        if (wasEmpty) {
            scheduleNextPendingBirthAttempt(true);
        }
    }

    private void scheduleNextPendingBirthAttempt(boolean successfulLastAttempt) {
        long now = this.getWorld().getTime();
        if (successfulLastAttempt) {

            this.nextPendingBirthTick = now + 18000L + this.random.nextInt(12000);
        } else {
            this.nextPendingBirthTick = now + 600L + this.random.nextInt(1200);
        }
    }

    /** Обробляє подію життєвого циклу. */
    protected void onBabyCreated(HumanoidEntity baby) {
    }

    private boolean isFleeRace() {
        return switch (getRace().getDangerBehavior()) {
            case FLEE -> true;
            case FIGHT -> false;
        };
    }

    private boolean isFightRace() {
        return !isFleeRace();
    }

    /** Налаштовує цілі поведінки. */
    @Override
    protected void initGoals() {
        super.initGoals();
        GoalSelector goals = this.goalSelector;
        goals.add(3, new FindMateGoal(this));
        goals.add(3, new PickUpFoodGoal(this));
        goals.add(3, new AcquireProfessionGoal(this));
        goals.add(4, new hik1tka.risen_races.entity.humanoid.goal.ShareFoodGoal(this));
        goals.add(6, new WanderAroundFarGoal(this, 0.6D));
        goals.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        goals.add(8, new LookAroundGoal(this));
        goals.add(1, new ConditionalGoal(
                new PanicUntilSafeGoal(this, 1.3D, 5),
                this::isFleeRace));

        goals.add(1, new ConditionalGoal(
                new FleeEntityGoal<>(this, HostileEntity.class, 8.0F, 1.0D, 1.2D),
                this::isFleeRace));


        goals.add(1, new ConditionalGoal(
                new RizenPiglinDefenseGoal(this),
                this::isFightRace));

        goals.add(2, new ConditionalGoal(
                new MeleeAttackGoal(this, 1.2D, false),
                this::isFightRace));
    }

    /** Повертає поточне значення властивості. */
    @Override
    protected SoundEvent getAmbientSound() {
        return getRace().getAmbientSound();
    }

    /** Повертає поточне значення властивості. */
    @Override
    protected SoundEvent getHurtSound(net.minecraft.entity.damage.DamageSource source) {
        return getRace().getHurtSound();
    }

    /** Повертає поточне значення властивості. */
    @Override
    protected SoundEvent getDeathSound() {
        return getRace().getDeathSound();
    }

    /** Зберігає стан у NBT. */
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Race", getRace().ordinal());
        nbt.putBoolean("IsFemale", isFemale());
        nbt.putInt("BreedingCooldown", breedingCooldown);
        nbt.putInt("PendingBabies", pendingBabies);
        if (pendingBabies > 0) {
            nbt.putLong("NextPendingBirthTick", nextPendingBirthTick);
        }
        nbt.putString("Profession", getProfession());
        if (jobSite != null) {
            nbt.put("JobSite", net.minecraft.nbt.NbtHelper.fromBlockPos(jobSite));
        }

        net.minecraft.util.collection.DefaultedList<ItemStack> items =
                net.minecraft.util.collection.DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            items.set(i, inventory.getStack(i));
        }
        NbtCompound inventoryNbt = new NbtCompound();
        Inventories.writeNbt(inventoryNbt, items);
        nbt.put("HumanoidInventory", inventoryNbt);
    }

    /** Відновлює стан з NBT. */
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("Race")) {
            setRace(HumanoidRace.values()[nbt.getInt("Race")]);
        }
        if (nbt.contains("IsFemale")) {
            setFemale(nbt.getBoolean("IsFemale"));
        }
        if (nbt.contains("Profession")) {
            setProfession(nbt.getString("Profession"));
        }
        if (nbt.contains("JobSite")) {
            this.jobSite = net.minecraft.nbt.NbtHelper.toBlockPos(nbt.getCompound("JobSite"));
        }
        this.breedingCooldown = nbt.getInt("BreedingCooldown");
        this.pendingBabies = nbt.getInt("PendingBabies");
        this.nextPendingBirthTick = this.pendingBabies > 0 && nbt.contains("NextPendingBirthTick")
                ? nbt.getLong("NextPendingBirthTick")
                : -1L;

        if (nbt.contains("HumanoidInventory")) {
            net.minecraft.util.collection.DefaultedList<ItemStack> items =
                    net.minecraft.util.collection.DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
            Inventories.readNbt(nbt.getCompound("HumanoidInventory"), items);
            for (int i = 0; i < items.size(); i++) {
                inventory.setStack(i, items.get(i));
            }
        }
    }

    /** Оновлює стан сутності щотік. */
    @Override
    public void tick() {
        super.tick();
        if (!getWorld().isClient()) {
            if (breedingCooldown > 0) {
                breedingCooldown--;
            }

            if (pendingBabies > 0 && getWorld() instanceof ServerWorld pendingBirthWorld
                    && getWorld().getTime() >= nextPendingBirthTick) {
                if (VillageCapacityHelper.getAvailableRoom(this) > 0) {
                    spawnOneBaby(pendingBirthWorld);
                    pendingBabies--;


                    VillageCapacityHelper.announceIfFull(this);
                    if (pendingBabies > 0) {
                        scheduleNextPendingBirthAttempt(true);
                    } else {
                        nextPendingBirthTick = -1L;
                    }
                } else {


                    scheduleNextPendingBirthAttempt(false);
                }
            }

            if (jobSite != null && this.age % 20 == 0
                    && getWorld() instanceof ServerWorld serverWorld) {
                boolean stillValid = getAvailableProfessions().stream()
                        .filter(p -> p.id().equals(getProfession()))
                        .findFirst()
                        .map(p -> serverWorld.getPointOfInterestStorage().test(jobSite, p.workstationPredicate()))
                        .orElse(false);
                if (!stillValid) {
                    clearJobSite();
                }
            }
        }
    }

    /** Виконує дію компонента. */
    @Override
    protected void fillRecipes() {
    }

    /** Перевіряє поточну умову. */
    @Override
    public boolean isClient() {
        return this.getWorld().isClient();
    }
}
