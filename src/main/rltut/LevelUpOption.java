package rltut;

public abstract class LevelUpOption {
    private String name;

    public LevelUpOption(String name) {
        this.name = name;
    }

    public String name() { return name; }

    public abstract void invoke(Creature creature);
}
