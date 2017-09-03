package rltut;

import java.awt.Color;

public class Creature {
    private char glyph;
    private CreatureAi ai;
    private Color color;
    private World world;

    public int x;
    public int y;

    public Creature(World world, char glyph, Color color) {
        this.world = world;
        this.glyph = glyph;
        this.color = color;
    }

    public char glyph() { return glyph; }
    public Color color() { return color; }

    public void setCreatureAi(CreatureAi ai) { this.ai = ai; }

    public void dig(int wx, int wy) {
        world.dig(wx, wy);
    }

    public void moveBy(int mx, int my) {
        ai.onEnter(x + mx, y + my, world.tile(x + mx, y + my));
    }
}
