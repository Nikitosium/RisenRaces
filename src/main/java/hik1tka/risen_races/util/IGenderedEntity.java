package hik1tka.risen_races.util;

import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.util.math.random.Random;

public interface IGenderedEntity {
    boolean isFemale();
    void setFemale(boolean female);
    boolean isInLove();
    void setLoveTicks(int ticks);
    boolean canBreedWith(PassiveEntity other);
    String getRaceId();

    static void generateRandomGender(IGenderedEntity entity, Random random) {
        boolean isFemale = random.nextFloat() < 0.51f;
        entity.setFemale(isFemale);
    }

    default boolean canBreedWithGendered(PassiveEntity other) {

        if (other instanceof IGenderedEntity otherGendered) {


            if (!this.getRaceId().equals(otherGendered.getRaceId())) {
                return false;
            }



            return this.isFemale() != otherGendered.isFemale();
        }


        return false;
    }

}
