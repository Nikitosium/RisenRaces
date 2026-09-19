package hik1tka.risen_races.entity.humanoid.data;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.function.Predicate;









/** Виконує дію компонента. */
public record ProfessionDefinition(
        String id,
        Predicate<RegistryEntry<PointOfInterestType>> workstationPredicate,
        int searchRadius
) {
    public ProfessionDefinition(String id, RegistryKey<PointOfInterestType> poiType, int searchRadius) {
        this(id, entry -> entry.matchesKey(poiType), searchRadius);
    }
}
