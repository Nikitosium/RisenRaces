package hik1tka.risen_races.util;

import hik1tka.risen_races.entity.zombie.ZombifiedHumanDrownedEntity;
import hik1tka.risen_races.entity.zombie.ZombifiedHumanEntity;
import hik1tka.risen_races.entity.zombie.ZombifiedHumanHuskEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public final class ZombieVariantHelper {

    private ZombieVariantHelper() {
    }

    private static final Set<RegistryKey<Biome>> HUSK_BIOMES = Set.of(
            BiomeKeys.DESERT,
            BiomeKeys.SAVANNA, BiomeKeys.SAVANNA_PLATEAU, BiomeKeys.WINDSWEPT_SAVANNA,
            BiomeKeys.JUNGLE, BiomeKeys.SPARSE_JUNGLE, BiomeKeys.BAMBOO_JUNGLE
    );

    private static final Set<RegistryKey<Biome>> DROWNED_BIOMES = Set.of(
            BiomeKeys.OCEAN, BiomeKeys.DEEP_OCEAN,
            BiomeKeys.COLD_OCEAN, BiomeKeys.DEEP_COLD_OCEAN,
            BiomeKeys.LUKEWARM_OCEAN, BiomeKeys.DEEP_LUKEWARM_OCEAN,
            BiomeKeys.WARM_OCEAN,
            BiomeKeys.FROZEN_OCEAN, BiomeKeys.DEEP_FROZEN_OCEAN,
            BiomeKeys.RIVER, BiomeKeys.FROZEN_RIVER,
            BiomeKeys.SWAMP, BiomeKeys.MANGROVE_SWAMP,
            BiomeKeys.BEACH, BiomeKeys.SNOWY_BEACH, BiomeKeys.STONY_SHORE
    );

    /** Знаходить потрібний обєкт. */
    public static ZombieVariant resolveVariant(ServerWorld world, Entity entity) {


        if (entity.isTouchingWater()) {
            return ZombieVariant.DROWNED;
        }

        BlockPos pos = entity.getBlockPos();
        RegistryEntry<Biome> biome = world.getBiome(pos);

        for (RegistryKey<Biome> key : DROWNED_BIOMES) {
            if (biome.matchesKey(key)) {
                return ZombieVariant.DROWNED;
            }
        }
        for (RegistryKey<Biome> key : HUSK_BIOMES) {
            if (biome.matchesKey(key)) {
                return ZombieVariant.HUSK;
            }
        }
        return ZombieVariant.NORMAL;
    }

    /** Створює потрібний обєкт або сутність. */
    @Nullable
    public static ZombieEntity create(ServerWorld world, ZombieVariant variant) {
        return switch (variant) {
            case HUSK -> ZombifiedHumanHuskEntity.ZOMBIFIED_HUMAN_HUSK.create(world);
            case DROWNED -> ZombifiedHumanDrownedEntity.ZOMBIFIED_HUMAN_DROWNED.create(world);
            case NORMAL -> ZombifiedHumanEntity.ZOMBIFIED_HUMAN.create(world);
        };
    }
}
