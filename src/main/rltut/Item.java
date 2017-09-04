package rltut;

import java.awt.*;

public class Item {
    private char glyph;
    private Color color;
    private String name;

    public Item(char glyph, Color color, String name) {
        this.glyph = glyph;
        this.color = color;
        this.name = name;
    }

    public char glyph() { return glyph; }

    public Color color() { return color; }

    public String name() { return name; }
}
