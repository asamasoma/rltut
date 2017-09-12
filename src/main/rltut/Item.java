package rltut;

import java.awt.*;

public class Item {
    private char glyph;
    private Color color;
    private String name;
    private int attackValue;
    private int thrownAttackValue;
    private int rangedAttackValue;
    private int defenseValue;
    private int foodValue;
    private Effect quaffEffect;

    public Item(char glyph, Color color, String name) {
        this.glyph = glyph;
        this.color = color;
        this.name = name;
        this.thrownAttackValue = 1;
    }

    public char glyph() { return glyph; }

    public Color color() { return color; }

    public String name() { return name; }

    public int attackValue() { return attackValue; }

    public int thrownAttackValue() { return thrownAttackValue; }

    public int rangedAttackValue() { return rangedAttackValue; }

    public int defenseValue() { return defenseValue; }

    public int foodValue() { return foodValue; }

    public Effect quaffEffect() { return quaffEffect; }

    public void modifyAttackValue(int amount) { attackValue += amount; }

    public void modifyThrownAttackValue(int amount) { thrownAttackValue += amount; }

    public void modifyRangedAttackValue(int amount) { rangedAttackValue += amount; }

    public void modifyDefenseValue(int amount) { defenseValue += amount; }

    public void modifyFoodValue(int amount) { foodValue += amount; }

    public void setQuaffEffect(Effect effect) { this.quaffEffect = effect; }

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
