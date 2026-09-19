package hik1tka.risen_races.register;

import hik1tka.risen_races.entity.zombie.IZombifiedHuman;
import hik1tka.risen_races.entity.zombie.ZombifiedHumanDrownedEntity;
import hik1tka.risen_races.entity.zombie.ZombifiedHumanHuskEntity;
import hik1tka.risen_races.util.ZombieVariant;
import hik1tka.risen_races.util.ZombieVariantHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;

/**
 * Той самий принцип, що ModVillagerReplacement, тільки для зомбі-родини:
 *  - ховає ZOMBIE_/HUSK_/DROWNED_SPAWN_EGG з креативної вкладки
 *  - будь-який ТОЧНО ZombieEntity/HuskEntity/DrownedEntity (getClass() ==,
 *    не підклас - тому наш власний зомбі себе не зачіпляє), що з'являється у
 *    світі (природний спавн, спавнер, або саме яйце спавну - ENTITY_LOAD
 *    ловить усі шляхи однаково) - видаляється і замінюється нашим.
 *
 * ZombieEntity -> варіант обирається за біомом (ZombieVariantHelper), бо
 * ванільний звичайний зомбі сам по собі не несе інформації "я мав би бути
 * кадавром/утопцем". А ось якщо вже заспавнився САМЕ HuskEntity чи
 * DrownedEntity (природно чи через власне яйце) - ванілла вже прийняла це
 * рішення за нас, тому мапимо напряму, без повторного біом-чеку.
 *
 * rollRandomSpawnData() викликається тут ЯВНО - ENTITY_LOAD не проходить
 * через initialize() (той шлях спрацьовує лише для мобспавнера/природного
 * спавну напряму), тому без явного виклику заміщені зомбі лишались би з
 * дефолтними значеннями.
 */
public class ModZombieReplacement {

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
            entries.getDisplayStacks().removeIf(ModZombieReplacement::isZombieFamilyEgg);
            entries.getSearchTabStacks().removeIf(ModZombieReplacement::isZombieFamilyEgg);
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity.getClass() == ZombieEntity.class) {
                replaceZombie(entity, world);
            } else if (entity.getClass() == HuskEntity.class) {
                replaceWith(entity, world, ZombifiedHumanHuskEntity.ZOMBIFIED_HUMAN_HUSK.create(world));
            } else if (entity.getClass() == DrownedEntity.class) {
                replaceWith(entity, world, ZombifiedHumanDrownedEntity.ZOMBIFIED_HUMAN_DROWNED.create(world));
            }
        });
    }

    private static boolean isZombieFamilyEgg(net.minecraft.item.ItemStack stack) {
        return stack.getItem() == Items.ZOMBIE_SPAWN_EGG
                || stack.getItem() == Items.HUSK_SPAWN_EGG
                || stack.getItem() == Items.DROWNED_SPAWN_EGG;
    }

    private static void replaceZombie(Entity entity, ServerWorld world) {
        ZombieVariant variant = ZombieVariantHelper.resolveVariant(world, entity);
        ZombieEntity zombie = ZombieVariantHelper.create(world, variant);
        replaceWith(entity, world, zombie);
    }

    private static void replaceWith(Entity original, ServerWorld world, ZombieEntity replacement) {
        if (replacement == null) return;

        replacement.refreshPositionAndAngles(original.getX(), original.getY(), original.getZ(),
                original.getYaw(), original.getPitch());

        if (replacement instanceof IZombifiedHuman zombified) {
            zombified.rollRandomSpawnData();
        }

        original.discard();
        world.spawnEntity(replacement);
    }
}