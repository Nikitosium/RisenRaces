package hik1tka.risen_races.register;

import hik1tka.risen_races.entity.zombie.IZombifiedHuman;
import hik1tka.risen_races.entity.zombie.ZombifiedHumanDrownedEntity;
import hik1tka.risen_races.entity.zombie.ZombifiedHumanHuskEntity;
import hik1tka.risen_races.util.ZombieVariant;
import hik1tka.risen_races.util.ZombieVariantHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.world.ServerWorld;

public class ModZombieReplacement {

    public static void register() {
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

    private static void replaceZombie(Entity entity, ServerWorld world) {
        ZombieVariant variant = ZombieVariantHelper.resolveVariant(world, entity);
        ZombieEntity zombie = ZombieVariantHelper.create(world, variant);
        replaceWith(entity, world, zombie);
    }

    private static void replaceWith(Entity original, ServerWorld world, ZombieEntity replacement) {
        if (replacement == null) return;

        replacement.refreshPositionAndAngles(original.getX(), original.getY(), original.getZ(),
                original.getYaw(), original.getPitch());

        // Заміна має успадкувати вік ванільного моба. Інакше baby zombie,
        // drowned або husk після підміни стає дорослим кастомним зомбі.
        if (original instanceof ZombieEntity originalZombie) {
            replacement.setBaby(originalZombie.isBaby());
        }

        if (replacement instanceof IZombifiedHuman zombified) {
            zombified.rollRandomSpawnData();
        }

        original.discard();
        world.spawnEntity(replacement);
    }
}
