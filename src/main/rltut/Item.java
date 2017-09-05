package rltut;

import java.awt.*;

public class Item {
    private char glyph;
    private Color color;
    private String name;
    private int attackValue;
    private int defenseValue;
    private int foodValue;

    public Item(char glyph, Color color, String name) {
        this.glyph = glyph;
        this.color = color;
        this.name = name;
    }

    public char glyph() { return glyph; }

    public Color color() { return color; }

    public String name() { return name; }

    public int attackValue() { return attackValue; }

    public int defenseValue() { return defenseValue; }

    public int foodValue() { return foodValue; }

    public void modifyAttackValue(int amount) { attackValue += amount; }

    public void modifyDefenseValue(int amount) { defenseValue += amount; }

    public void modifyFoodValue(int amount) { foodValue += amount; }
}
