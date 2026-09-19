package hik1tka.risen_races.entity.zombie;

import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;












public interface IZombifiedHuman {
    boolean isFemale();
    void setFemale(boolean female);
    String getProfession();
    void setProfession(String profession);
    @Nullable NbtCompound getNpcMemory();
    void setNpcMemory(@Nullable NbtCompound memory);
    void rollRandomSpawnData();
    boolean isBaby();
}
