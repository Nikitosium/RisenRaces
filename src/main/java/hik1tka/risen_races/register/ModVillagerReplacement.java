package hik1tka.risen_races.register;

import hik1tka.risen_races.entity.humanoid.rynar.RynarEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemGroups;














public class ModVillagerReplacement {

    /** Реєструє компонент модуля. */
    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
            entries.getDisplayStacks().removeIf(stack -> stack.getItem() == Items.VILLAGER_SPAWN_EGG);
            entries.getSearchTabStacks().removeIf(stack -> stack.getItem() == Items.VILLAGER_SPAWN_EGG);
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity.getClass() != VillagerEntity.class) return;

            RynarEntity rynar = RynarEntity.RYNAR.create(world);
            if (rynar == null) return;

            rynar.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(),
                    entity.getYaw(), entity.getPitch());

            entity.discard();
            world.spawnEntity(rynar);
        });
    }
}
