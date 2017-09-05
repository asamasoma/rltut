package rltut;

import asciiPanel.AsciiPanel;

import java.util.List;

public class StuffFactory {
    private World world;
    private FieldOfView fov;

    public StuffFactory(World world, FieldOfView fov) {
        this.world = world;
        this.fov = fov;
    }

    public Creature newPlayer(List<String> messages) {
        Creature player = new Creature(world, "player", '@', AsciiPanel.brightWhite, 100, 20, 5);
        world.addAtEmptyLocation(player, 0);
        new PlayerAi(player, messages, fov);
        return player;
    }

    public Creature newBat(int depth) {
        Creature bat = new Creature(world, "bat", 'b', AsciiPanel.yellow, 15, 5, 0);
        world.addAtEmptyLocation(bat, depth);
        new BatAi(bat);
        return bat;
    }

    public Creature newFungus(int depth) {
        Creature fungus = new Creature(world, "fungus", 'f', AsciiPanel.green, 10, 0, 0);
        world.addAtEmptyLocation(fungus, depth);
        new FungusAi(fungus, this);
        return fungus;
    }

    public Item newVictoryItem(int depth) {
        Item item = new Item('*', AsciiPanel.brightWhite, "teddy bear");
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newRock(int depth) {
        Item rock = new Item(',', AsciiPanel.yellow, "rock");
        world.addAtEmptyLocation(rock, depth);
        return rock;
    }
}
