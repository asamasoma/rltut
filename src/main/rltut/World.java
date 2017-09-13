package rltut;

import java.awt.Color;

import java.util.ArrayList;
import java.util.List;

public class World {
    private Tile[][][] tiles;
    private Item[][][] items;
    private int width;
    private int height;
    private int depth;
    private List<Creature> creatures;

    public World(Tile[][][] tiles) {
        this.tiles = tiles;
        this.width = tiles.length;
        this.height = tiles[0].length;
        this.depth = tiles[0][0].length;
        this.items = new Item[width][height][depth];
        this.creatures = new ArrayList<>();
    }

    public int width() { return width; }
    public int height() { return height; }
    public int depth() { return depth; }

    public Tile tile(int x, int y, int z) {
        return isOutsideBounds(x, y, z) ? Tile.BOUNDS : tiles[x][y][z];
    }

    public Item item(int x, int y, int z) { return items[x][y][z]; }

    public char glyph(int x, int y, int z) {
        if (item(x, y, z) != null)
            return item(x, y, z).glyph();
        return tile(x, y, z).glyph();
    }

    public Color color(int x, int y, int z) {
        if (item(x, y, z) != null)
            return item(x, y, z).color();
        return tile(x, y, z).color();
    }

    public Creature creature(int x, int y, int z) {
        for (Creature c : creatures) {
            if (c.x == x && c.y == y && c.z == z)
                return c;
        }
        return null;
    }

    public List<Creature> creatures() {
        return creatures;
    }

    public void dig(int x, int y, int z) {
        if (tile(x, y, z).isDiggable()) tiles[x][y][z] = Tile.FLOOR;
    }

    public void add(Creature creature) {
        creatures.add(creature);
    }

    public void addAtEmptyLocation(Creature creature, int depth) {
        // TODO: Fix this so it bails if there are no empty locations
        int x;
        int y;

        do {
            x = (int)(Math.random() * width);
            y = (int)(Math.random() * height);
        } while (!tile(x,y,depth).isGround() || creature(x,y,depth) != null);

        creature.x = x;
        creature.y = y;
        creature.z = depth;
        creatures.add(creature);
    }

    public void addAtEmptyLocation(Item item, int depth) {
        // TODO: Fix this so it bails if there are no empty locations
        int x;
        int y;

        do {
            x = (int)(Math.random() * width);
            y = (int)(Math.random() * height);
        } while (!tile(x,y,depth).isGround() || item(x,y,depth) != null);

        items[x][y][depth] = item;
    }

    public boolean addAtEmptySpace(Item item, int x, int y, int z) {
        if (item == null)
            return true;

        List<Point> points = new ArrayList<>();
        List<Point> checked = new ArrayList<>();

        points.add(new Point(x, y, z));

        while (!points.isEmpty()) {
            Point p = points.remove(0);
            checked.add(p);

            if (!tile(p.x, p.y, p.z).isGround())
                continue;

            if (items[p.x][p.y][p.z] == null) {
                items[p.x][p.y][p.z] = item;
                Creature c = this.creature(p.x, p.y, p.z);
                if (c != null)
                    c.notify("A %s lands between your feet.", item.name());
                return true;
            } else {
                List<Point> neighbors = p.neighbors();
                neighbors.removeAll(checked);
                points.addAll(neighbors);
            }
        }
        return false;
    }

    public void remove(Creature creature) {
        creatures.remove(creature);
    }

    public void remove(Item item) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    if (items[x][y][z] == item) {
                        items[x][y][z] = null;
                        return;
                    }
                }
            }
        }
    }

    public void remove(int x, int y, int z) {
        items[x][y][z] = null;
    }

    public void update() {
        List<Creature> toUpdate = new ArrayList<>(creatures);
        for (Creature creature : toUpdate) {
            creature.update();
        }
    }

    private boolean isOutsideBounds(int x, int y, int z) {
        return x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= depth;
    }
}
