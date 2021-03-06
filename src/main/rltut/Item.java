package rltut;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Item {
    private char glyph;
    private Color color;
    private String name;
    private String appearance;
    private int attackValue;
    private int thrownAttackValue;
    private int rangedAttackValue;
    private int defenseValue;
    private int foodValue;
    private Effect quaffEffect;
    private List<Spell> writtenSpells;

    public Item(char glyph, Color color, String name, String appearance) {
        this.glyph = glyph;
        this.color = color;
        this.name = name;
        this.appearance = appearance;
        this.thrownAttackValue = 1;
        this.writtenSpells = new ArrayList<>();
    }

    public char glyph() { return glyph; }

    public Color color() { return color; }

    public String name() { return name; }

    public String appearance() {
        if (appearance == null)
            return name;

        return appearance;
    }

    public int attackValue() { return attackValue; }

    public int thrownAttackValue() { return thrownAttackValue; }

    public int rangedAttackValue() { return rangedAttackValue; }

    public int defenseValue() { return defenseValue; }

    public int foodValue() { return foodValue; }

    public Effect quaffEffect() { return quaffEffect; }

    public List<Spell> writtenSpells() { return writtenSpells; }

    public void modifyAttackValue(int amount) { attackValue += amount; }

    public void modifyThrownAttackValue(int amount) { thrownAttackValue += amount; }

    public void modifyRangedAttackValue(int amount) { rangedAttackValue += amount; }

    public void modifyDefenseValue(int amount) { defenseValue += amount; }

    public void modifyFoodValue(int amount) { foodValue += amount; }

    public void setQuaffEffect(Effect effect) { this.quaffEffect = effect; }

    public void addWrittenSpell(String name, int manaCost, Effect effect) {
        writtenSpells.add(new Spell(name, manaCost, effect));
    }

    public String details() {
        String details = "";

        if (attackValue != 0)
            details += "     attack: " + attackValue;

        if (thrownAttackValue != 0)
            details += "     thrown: " + thrownAttackValue;

        if (rangedAttackValue != 0)
            details += " ranged: " + rangedAttackValue;

        if (defenseValue != 0)
            details += "     defense: " + defenseValue;

        if (foodValue != 0)
            details += "     food: " + foodValue;

        return details;
    }
}
