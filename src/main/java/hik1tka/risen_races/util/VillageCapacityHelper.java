package hik1tka.risen_races.util;

import hik1tka.risen_races.entity.humanoid.HumanoidEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;

import java.util.Map;
import java.util.WeakHashMap;

public class VillageCapacityHelper {

    private static final double SEARCH_RADIUS = 64.0D;
    private static final long NOON_TICK = 6000L;

    private static final Map<ServerWorld, Long> lastAnnouncedDay = new WeakHashMap<>();

    /** Повертає поточне значення властивості. */
    public static int getAvailableRoom(HumanoidEntity self) {
        if (!(self.getWorld() instanceof ServerWorld world)) return Integer.MAX_VALUE;

        Box area = self.getBoundingBox().expand(SEARCH_RADIUS);

        long beds = world.getPointOfInterestStorage().getInSquare(
                entry -> entry.matchesKey(PointOfInterestTypes.HOME),
                self.getBlockPos(),
                (int) SEARCH_RADIUS,
                PointOfInterestStorage.OccupationStatus.ANY
        ).count();

        long population = world.getEntitiesByClass(self.getClass(), area, e -> true).size();

        return (int) Math.max(0L, beds - population);
    }

    /** Перевіряє поточну умову. */
    public static boolean hasRoomToBreed(HumanoidEntity self) {
        int room = getAvailableRoom(self);
        if (room > 0) return true;

        maybeAnnounce(self.getWorld(), -room + 1);
        return false;
    }

    /** Виконує дію компонента. */
    public static int capBabyCount(HumanoidEntity self, int requestedBabies) {
        return Math.min(requestedBabies, getAvailableRoom(self));
    }

    /** Виконує дію компонента. */
    public static void announceIfFull(HumanoidEntity self) {
        if (!(self.getWorld() instanceof ServerWorld world)) return;

        int room = getAvailableRoom(self);
        if (room > 0) return;

        announceNow(world, -room + 1);
    }

    /** Виконує дію компонента. */
    public static void announceQueuedBirths(HumanoidEntity self, int queuedCount) {
        if (queuedCount <= 0) return;
        if (!(self.getWorld() instanceof ServerWorld world)) return;
        announceNow(world, queuedCount);
    }

    private static void maybeAnnounce(net.minecraft.world.World genericWorld, int shortage) {
        if (!(genericWorld instanceof ServerWorld world)) return;

        long timeOfDay = world.getTimeOfDay() % 24000L;
        if (timeOfDay < NOON_TICK) return;

        announceNow(world, shortage);
    }

    private static void announceNow(ServerWorld world, int shortage) {
        long day = world.getTimeOfDay() / 24000L;
        Long alreadyShown = lastAnnouncedDay.get(world);
        if (alreadyShown != null && alreadyShown == day) return;
        lastAnnouncedDay.put(world, day);

        Text settlement = getSettlementName();
        Text message = Text.translatable("risen_races.overpopulation", settlement, shortage, settlement);
        for (ServerPlayerEntity player : world.getPlayers()) {
            player.sendMessage(message, false);
        }
    }

    private static Text getSettlementName() {
        return Text.translatable("risen_races.settlement.default");
    }
}
