package hik1tka.risen_races.register;

import hik1tka.risen_races.entity.zombie.IZombifiedHuman;
import hik1tka.risen_races.entity.zombie.ZombifiedHumanDrownedEntity;
import hik1tka.risen_races.entity.zombie.ZombifiedHumanHuskEntity;
import hik1tka.risen_races.util.ZombieVariant;
import hik1tka.risen_races.util.ZombieVariantHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;

/**
 * Той самий принцип, що ModVillagerReplacement, тільки для зомбі:
 *  - ховає ZOMBIE_SPAWN_EGG/HUSK_SPAWN_EGG/DROWNED_SPAWN_EGG з креативної вкладки
 *  - будь-який ТОЧНО ZombieEntity (не Husk/Drowned/наш власний зомбі -
 *    getClass() != ZombieEntity.class відсікає підкласи), що з'являється у
 *    світі - видаляється і замінюється відповідним підвидом
 *    ZombifiedHumanEntity (звичайний/кадавр/утопець - за біомом спавну,
 *    див. ZombieVariantHelper.resolveVariant()).
 *  - будь-який ТОЧНО ванільний HuskEntity чи DrownedEntity (природний спавн
 *    у пустелі/океані - вони спавняться СВОЇМ типом напряму, ніколи не як
 *    ZombieEntity, тому перший пункт їх не ловив) - видаляється й заміняється
 *    відповідним нашим підвидом один-в-один (без визначення біома - ваніль
 *    вже сама вирішила, що це має бути кадавр чи утопець).
 *
 * rollRandomSpawnData() викликається тут ЯВНО - ENTITY_LOAD не проходить
 * через initialize() (той шлях спрацьовує лише для мобспавнера/природного
 * спавну напряму), тому без явного виклику заміщені зомбі лишались би з
 * дефолтними значеннями.
 */
public class ModZombieReplacement {

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
            entries.getDisplayStacks().removeIf(stack ->
                    stack.getItem() == Items.ZOMBIE_SPAWN_EGG
                            || stack.getItem() == Items.HUSK_SPAWN_EGG
                            || stack.getItem() == Items.DROWNED_SPAWN_EGG);
            entries.getSearchTabStacks().removeIf(stack ->
                    stack.getItem() == Items.ZOMBIE_SPAWN_EGG
                            || stack.getItem() == Items.HUSK_SPAWN_EGG
                            || stack.getItem() == Items.DROWNED_SPAWN_EGG);
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            // Звичайний ZombieEntity - варіант вирішується біомом/водою.
            if (entity.getClass() == ZombieEntity.class) {
                ZombieVariant variant = ZombieVariantHelper.resolveVariant(world, entity);
                ZombieEntity zombie = ZombieVariantHelper.create(world, variant);
                if (zombie == null) return;

                zombie.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(),
                        entity.getYaw(), entity.getPitch());
                if (zombie instanceof IZombifiedHuman zombified) {
                    zombified.rollRandomSpawnData();
                }

                entity.discard();
                world.spawnEntity(zombie);
                return;
            }

            // Ванільний Husk - спавнився вже конкретно кадавром, варіант відомий напряму.
            if (entity.getClass() == HuskEntity.class) {
                ZombifiedHumanHuskEntity husk = ZombifiedHumanHuskEntity.ZOMBIFIED_HUMAN_HUSK.create(world);
                if (husk == null) return;

                husk.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(),
                        entity.getYaw(), entity.getPitch());
                husk.rollRandomSpawnData();

                entity.discard();
                world.spawnEntity(husk);
                return;
            }

            // Ванільний Drowned - так само, спавнився вже конкретно утопцем.
            if (entity.getClass() == DrownedEntity.class) {
                ZombifiedHumanDrownedEntity drowned = ZombifiedHumanDrownedEntity.ZOMBIFIED_HUMAN_DROWNED.create(world);
                if (drowned == null) return;

                drowned.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(),
                        entity.getYaw(), entity.getPitch());
                drowned.rollRandomSpawnData();

                entity.discard();
                world.spawnEntity(drowned);
            }
        });
    }
}