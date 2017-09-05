package rltut;

import java.awt.Color;

public class Creature {
    private String name;
    private char glyph;
    private CreatureAi ai;
    private Color color;
    private Inventory inventory;
    private World world;
    private Item weapon;
    private Item armor;
    private int maxHp;
    private int hp;
    private int maxFood;
    private int food;
    private int attackValue;
    private int defenseValue;
    private int visionRadius;

    public int x;
    public int y;
    public int z;

    public Creature(World world, String name, char glyph, Color color, int maxHp, int attack, int defense) {
        this.world = world;
        this.name = name;
        this.glyph = glyph;
        this.color = color;
        this.inventory = new Inventory(20);
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.maxFood = 1000;
        this.food = maxFood / 3 * 2;
        this.attackValue = attack;
        this.defenseValue = defense;
        this.visionRadius = 9;
    }

    public String name() { return name; }
    public char glyph() { return glyph; }
    public Color color() { return color; }
    public Inventory inventory() { return inventory; }
    public Item weapon() { return weapon; }
    public Item armor() { return armor; }
    public int maxHp() { return maxHp; }
    public int hp() { return hp; }
    public int maxFood() { return maxFood; }
    public int food() { return food; }

    public int attackValue() {
        return attackValue
                + (weapon == null ? 0 : weapon.attackValue())
                + (armor == null ? 0 : armor.attackValue());
    }

    public int defenseValue() {
        return defenseValue
                + (weapon == null ? 0 : weapon.defenseValue())
                + (armor == null ? 0 : armor.defenseValue());
    }

    public int visionRadius() { return visionRadius; }

    public void setCreatureAi(CreatureAi ai) { this.ai = ai; }

    public void eat(Item item) {
        if (item.foodValue() < 0)
            notify("Gross!");

        modifyFood(item.foodValue());
        inventory.remove(item);
        unequip(item);
    }

    public void dig(int wx, int wy, int wz) {
        modifyFood(-10);
        world.dig(wx, wy, wz);
        doAction("dig");
    }

    public void pickup() {
        modifyFood(-1);
        Item item = world.item(x, y, z);

        if (inventory.isFull() || item == null) {
            doAction("grab at the ground");
        } else {
            doAction("pickup a %s", item.name());
            world.remove(x, y, z);
            inventory.add(item);
        }
    }

    public void drop(Item item) {
        modifyFood(-1);
        if (world.addAtEmptySpace(item, x, y, z)) {
            doAction("drop a " + item.name());
            inventory.remove(item);
            unequip(item);
        } else {
            notify("There's nowhere to drop the %s.", item.name());
        }
    }

    public void unequip(Item item) {
        if (item == null)
            return;

        if (item == armor) {
            doAction("remove a" + item.name());
            armor = null;
        } else if (item == weapon) {
            doAction("put away a " + item.name());
            weapon = null;
        }
    }

    public void equip(Item item) {
        if (item.attackValue() == 0 && item.defenseValue() == 0)
            return;

        if (item.attackValue() >= item.defenseValue()) {
            unequip(weapon);
            doAction("wield a " + item.name());
            weapon = item;
        } else {
            unequip(armor);
            doAction("put on a " + item.name());
            armor = item;
        }
    }

    public void moveBy(int mx, int my, int mz) {
        if (mx == 0 && my == 0 && mz == 0)
            return;

        modifyFood(-1);
        Tile tile = world.tile(x + mx, y + my, z + mz);

        if (mz == -1) {
            if (tile == Tile.STAIRS_DOWN) {
                doAction("walk up the stairs to level %d", z + mz + 1);
            } else {
                doAction("try to go up but are stopped by the cave ceiling");
                return;
            }
        } else if (mz == 1) {
            if (tile == Tile.STAIRS_UP) {
                doAction("walk down the stairs to level %d", z + mz + 1);
            } else {
                doAction("try to go down but are stopped by the cave floor");
                return;
            }
        }

        Creature other = world.creature(x + mx, y + my, z + mz);
        if (other == null)
            ai.onEnter(x + mx, y + my, z + mz, world.tile(x + mx, y + my, z + mz));
        else
            attack(other);
    }

    public void attack(Creature other) {
        modifyFood(-1);
        int amount = Math.max(0, attackValue() - other.defenseValue());

        amount = (int)(Math.random() * amount) + 1;

        doAction("attack the %s for %d damage", other.name, amount);

        other.modifyHp(-amount);
    }

    public void modifyHp(int amount) {
        hp += amount;
        if(hp > maxHp) {
            hp = maxHp;
        } else if (hp < 1) {
            doAction("die");
            leaveCorpse();
            world.remove(this);
        }
    }

    public void modifyFood(int amount) {
        food += amount;
        if (food > maxFood) {
            maxFood = maxFood + food / 2;
            food = maxFood;
            notify("You can't believe your stomach can hold that much!");
            modifyHp(-1);
        } else if (food < 1 && isPlayer()) {
            modifyHp(-1000);
        }
    }

    public void update() {
        modifyFood(-1);
        ai.onUpdate();
    }

    public void doAction(String message, Object ... params) {
        // TODO: notify within LOS instead of fixed radius?
        int r = 9;
        for (int ox = -r; ox < r + 1; ox++) {
            for (int oy = -r; oy < r + 1; oy++) {
                if (ox * ox + oy * oy > r * r)
                    continue;

                Creature other = world.creature(x + ox, y + oy, z);

                if (other == null)
                    continue;

                if (other == this)
                    other.notify("You " + message + ".", params);

                else if (other.canSee(x, y, z))
                    other.notify(String.format("The %s %s.", name, makeSecondPerson(message)), params);
            }
        }
    }

    public void notify(String message, Object ... params) {
        ai.onNotify(String.format(message, params));
    }

    public boolean isPlayer() {
        return glyph == '@';
    }

    public boolean canEnter(int wx, int wy, int wz) {
        return world.tile(wx, wy, wz).isGround() && world.creature(wx, wy, wz) == null;
    }

    public boolean canSee(int wx, int wy, int wz) {
        return ai.canSee(wx, wy, wz);
    }

    public Creature creature(int wx, int wy, int wz) {
        return world.creature(wx, wy, wz);
    }

    public Tile tile(int wx, int wy, int wz) {
        return world.tile(wx, wy, wz);
    }

    //TODO: move to a 'message helper' class
    private String makeSecondPerson(String text) {
        String[] words = text.split(" ");
        words[0] = words[0] + "s";

        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            builder.append(" ");
            builder.append(word);
        }

        return builder.toString().trim();
    }

    private void leaveCorpse() {
        Item corpse = new Item('%', color, name + " corpse");
        corpse.modifyFoodValue(maxHp * 3);
        world.addAtEmptySpace(corpse, x, y, z);
    }
}
