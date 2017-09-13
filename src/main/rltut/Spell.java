package rltut;

public class Spell {
    private String name;
    private int manaCost;
    private Effect effect;

    public Spell(String name, int manaCost, Effect effect) {
        this.name = name;
        this.manaCost = manaCost;
        this.effect = effect;
    }

    public String name() { return name; }

    public int manaCost() { return manaCost; }

    // TODO: each spell should subclass Spell with a "newEffect" effect factory method instead of copy constructor pattern
    public Effect effect() { return new Effect(effect); }
}
