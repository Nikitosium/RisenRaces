package hik1tka.risen_races.entity.humanoid.data;

public class HumanoidData {
    private final String raceId;
    private final boolean isFemale;
    private final String profession;
    private final int level;

    public HumanoidData(String raceId, boolean isFemale, String profession, int level) {
        this.raceId = raceId;
        this.isFemale = isFemale;
        this.profession = profession;
        this.level = level;
    }

    /** Повертає поточне значення властивості. */
    public String getRaceId() { return raceId; }
    /** Перевіряє поточну умову. */
    public boolean isFemale() { return isFemale; }
    /** Повертає поточне значення властивості. */
    public String getProfession() { return profession; }
    /** Повертає поточне значення властивості. */
    public int getLevel() { return level; }
}
