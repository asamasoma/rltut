package rltut;

import java.awt.*;

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
    private int regenHpCooldown;
    private int regenHpPer1000;
    private int maxFood;
    private int food;
    private int xp;
    private int level;
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
        this.regenHpPer1000 = 1;
        this.regenHpPer1000 = 10;
        this.maxFood = 1000;
        this.food = maxFood / 3 * 2;
        this.xp = 0;
        this.level = 1;
        this.attackValue = attack;
        this.defenseValue = defense;
        this.visionRadius = 9;
    }

    public String name() {
        return name;
    }

    public String details() {
        return String.format("     level: %d     attack: %d     defense: %d     hp: %d",
                level, attackValue(), defenseValue(), hp);
    }

    public char glyph() {
        return glyph;
    }

    public Color color() {
        return color;
    }

    public Inventory inventory() {
        return inventory;
    }

    public Item weapon() {
        return weapon;
    }

    public Item armor() {
        return armor;
    }

    public int maxHp() {
        return maxHp;
    }

    public int hp() {
        return hp;
    }

    public int maxFood() {
        return maxFood;
    }

    public int food() {
        return food;
    }

    public int xp() {
        return xp;
    }

    public int level() {
        return level;
    }

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

    public int visionRadius() {
        return visionRadius;
    }

    public void setCreatureAi(CreatureAi ai) {
        this.ai = ai;
    }

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
            doAction("remove a " + item.name());
            armor = null;
        } else if (item == weapon) {
            doAction("put away a " + item.name());
            weapon = null;
        }
    }

    public void equip(Item item) {
        if (!inventory.contains(item)) {
            if (inventory.isFull()) {
                notify("Can't equip %s since you're holding too much stuff.", item.name());
                return;
            } else {
                world.remove(item);
                inventory.add(item);
            }
        }
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
            meleeAttack(other);
    }

    private void commonAttack(Creature other, int attack, String action, Object ... params) {
        modifyFood(-2);

        int amount = Math.max(0, attack - other.defenseValue());

        amount = (int)(Math.random() * amount) + 1;

        Object[] paramsCopy = new Object[params.length + 1];
        for (int i = 0; i < params.length; i++) {
            paramsCopy[i] = params[i];
        }
        paramsCopy[paramsCopy.length - 1] = amount;

        doAction(action, paramsCopy);

        other.modifyHp(-amount);

        if (other.hp < 1)
            gainXp(other);
    }

    public void meleeAttack(Creature other) {
        commonAttack(other, attackValue(), "attack the %s for %d damage", other.name);
    }

    public void throwAttack(Item item, Creature other) {
        commonAttack(other, attackValue / 2 + item.thrownAttackValue(), "throw a %s at the %s for %d damage", item.name(), other.name());
    }

    public void rangedWeaponAttack(Creature other) {
        commonAttack(other, attackValue / 2 + weapon.rangedAttackValue(), "fire a %s at the %s for %d damage", weapon.name(), other.name());
    }

    private void getRidOf(Item item) {
        inventory.remove(item);
        unequip(item);
    }

    private void putAt(Item item, int wx, int wy, int wz) {
        inventory.remove(item);
        unequip(item);
        world.addAtEmptySpace(item, wx, wy, wz);
    }

    public void throwItem(Item item, int wx, int wy, int wz) {
        Point end = new Point(x, y, 0);

        for (Point p : new Line(x, y, wx, wy)) {
            if (!realTile(p.x, p.y, z).isGround())
                break;
            end = p;
        }

        wx = end.x;
        wy = end.y;

        Creature c = creature(wx, wy, wz);

        if (c != null)
            throwAttack(item, c);
        else
            doAction("throw a %s", item.name());

        unequip(item);
        inventory.remove(item);
        world.addAtEmptySpace(item, wx, wy, wz);
    }

    public void modifyHp(int amount) {
        hp += amount;
        if (hp > maxHp) {
            hp = maxHp;
        } else if (hp < 1) {
            doAction("die");
            leaveCorpse();
            world.remove(this);
        }
    }

    public void modifyRegenHpPer1000(int amount) {
        regenHpPer1000 += amount;
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

    public void modifyXp(int amount) {
        xp += amount;

        notify("You %s %d xp.", amount < 0 ? "lose" : "gain", amount);

        while (xp > (int) (Math.pow(level, 1.5) * 20)) {
            level++;
            doAction("advance to level %d", level);
            ai.onGainLevel();
            modifyHp(level * 2);
        }
    }

    public void gainXp(Creature other) {
        int amount = other.maxHp
                + other.attackValue()
                + other.defenseValue()
                - level * 2;

        if (amount > 0)
            modifyXp(amount);
    }

    public void gainMaxHp() {
        maxHp += 10;
        hp += 10;
        doAction("look healthier");
    }

    public void gainAttackValue() {
        attackValue += 2;
        doAction("look stronger");
    }

    public void gainDefenseValue() {
        defenseValue += 2;
        doAction("look tougher");
    }

    public void gainVision() {
        visionRadius += 1;
        doAction("look more aware");
    }

    public void update() {
        modifyFood(-1);
        regenerateHealth();
        ai.onUpdate();
    }

    public void doAction(String message, Object... params) {
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

    public void notify(String message, Object... params) {
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

    public Tile realTile(int wx, int wy, int wz) {
        return world.tile(wx, wy, wz);
    }

    public Tile tile(int wx, int wy, int wz) {
        if (canSee(wx, wy, wz))
            return world.tile(wx, wy, wz);
        else
            return ai.rememberedTile(wx, wy, wz);
    }

    public Creature creature(int wx, int wy, int wz) {
        if (canSee(wx, wy, wz))
            return world.creature(wx, wy, wz);
        else
            return null;
    }

    public Item item(int wx, int wy, int wz) {
        if (canSee(wx, wy, wz))
            return world.item(wx, wy, wz);
        else
            return null;
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
        for (Item item : inventory.getItems()) {
            if (item != null)
                drop(item);
        }
    }

    // TODO: improve regeneration when leveling up
    private void regenerateHealth() {
        regenHpCooldown -= regenHpPer1000;
        if (regenHpCooldown < 0) {
            modifyHp(1);
            modifyFood(-1);
            regenHpCooldown += 1000;
        }
    }
}
