package rltut.screens;

import asciiPanel.AsciiPanel;
import rltut.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class PlayScreen implements Screen {
    private Screen subscreen;
    private World world;
    private Creature player;
    private FieldOfView fov;
    private int screenWidth;
    private int screenHeight;
    private List<String> messages;

    public PlayScreen() {
        screenWidth = 80;
        screenHeight = 21;
        messages = new ArrayList<>();
        createWorld();
        fov = new FieldOfView(world);

        StuffFactory stuffFactory = new StuffFactory(world, fov);
        createCreatures(stuffFactory);
        createItems(stuffFactory);
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
        int left = getScrollX();
        int top = getScrollY();

        displayTiles(terminal, left, top);
        displayMessages(terminal, messages);

        String stats = String.format(" %3d/%3d hp  %d/%d mana  %8s",
                player.hp(), player.maxHp(), player.mana(), player.maxMana(), hunger());
        terminal.write(stats, 1, 23);

        if (subscreen != null)
            subscreen.displayOutput(terminal);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        int level = player.level();

        if (subscreen != null) {
            subscreen = subscreen.respondToUserInput(key);
        } else {
            switch (key.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_H:
                    player.moveBy(-1, 0, 0);
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_L:
                    player.moveBy(1, 0, 0);
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_K:
                    player.moveBy(0, -1, 0);
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_J:
                    player.moveBy(0, 1, 0);
                    break;
                case KeyEvent.VK_Y:
                    player.moveBy(-1, -1, 0);
                    break;
                case KeyEvent.VK_U:
                    player.moveBy(1, -1, 0);
                    break;
                case KeyEvent.VK_B:
                    player.moveBy(-1, 1, 0);
                    break;
                case KeyEvent.VK_N:
                    player.moveBy(1, 1, 0);
                    break;
                case KeyEvent.VK_D: subscreen = new DropScreen(player); break;
                case KeyEvent.VK_E: subscreen = new EatScreen(player); break;
                case KeyEvent.VK_W: subscreen = new EquipScreen(player); break;
                case KeyEvent.VK_X: subscreen = new ExamineScreen(player); break;
                case KeyEvent.VK_SEMICOLON: subscreen = new LookScreen(player, "Looking", player.x - getScrollX(), player.y - getScrollY()); break;
                case KeyEvent.VK_T: subscreen = new ThrowScreen(player, player.x - getScrollX(), player.y - getScrollY()); break;
                case KeyEvent.VK_F:
                    if (player.weapon() == null || player.weapon().rangedAttackValue() == 0) //TODO: find a better place for this
                        player.notify("You don't have a ranged weapon equipped.");
                    else
                        subscreen = new FireWeaponScreen(player, player.x - getScrollX(), player.y - getScrollY()); break;
                case KeyEvent.VK_Q: subscreen = new QuaffScreen(player); break;
                case KeyEvent.VK_R: subscreen = new ReadScreen(player, player.x - getScrollX(), player.y - getScrollY()); break;
            }

            switch (key.getKeyChar()) {
                case 'g':
                case ',':
                    player.pickup();
                    break;
                case '<':
                    if (userIsTryingToExit())
                        return userExits();
                    else
                        player.moveBy(0, 0, -1);
                    break;
                case '>':
                    player.moveBy(0, 0, 1);
                    break;
                case '?':
                    subscreen = new HelpScreen(); break; //TODO: don't update world when leaving help screen
            }
        }

        if (player.level() > level)
            subscreen = new LevelUpScreen(player, player.level() - level);
        
        if (subscreen == null) world.update();

        if (player.hp() < 1)
            return new LoseScreen();

        return this;
    }

    public int getScrollX() {
        return Math.max(0, Math.min(player.x - screenWidth / 2, world.width() - screenWidth));
    }

    public int getScrollY() {
        return Math.max(0, Math.min(player.y - screenHeight / 2, world.height() - screenHeight));
    }

    private void createWorld() {
        world = new WorldBuilder(90, 31, 5)
                .makeCaves()
                .build();
    }

    private void createCreatures(StuffFactory stuffFactory) {
        player = stuffFactory.newPlayer(messages);

        for (int z = 0; z < world.depth(); z++) {
            for (int i = 0; i < 8; i++) {
                stuffFactory.newFungus(z);
            }

            for (int i = 0; i < 20; i++) {
                stuffFactory.newBat(z);
            }

            for (int i = 0; i < z + 3; i++) {
                stuffFactory.newZombie(z, player);
            }

            for (int i = 0; i < z + 1; i++) {
                stuffFactory.newGoblin(z, player);
            }
        }
    }

    private void createItems(StuffFactory factory) {
        for (int z = 0; z < world.depth(); z++) {
            for (int i = 0; i < world.width() * world.height() / 20; i++) {
                factory.newRock(z);
            }
            for (int i = 0; i < world.width() * world.height() / 300; i++) {
                factory.randomArmor(z);
                factory.randomWeapon(z);
                factory.randomPotion(z);
            }
            factory.newWhiteMagesSpellbook(z);
            factory.newBlueMagesSpellbook(z);
        }
        factory.newVictoryItem(world.depth() - 1);
    }

    private void displayTiles(AsciiPanel terminal, int left, int top) {
        fov.update(player.x, player.y, player.z, player.visionRadius());

        for (int x = 0; x < screenWidth; x++) {
            for (int y = 0; y < screenHeight; y++) {
                int wx = x + left;
                int wy = y + top;
                if (player.canSee(wx, wy, player.z)) {
                    terminal.write(world.glyph(wx, wy, player.z), x, y, world.color(wx, wy, player.z));
                } else {
                    terminal.write(fov.tile(wx, wy, player.z).glyph(), x, y, Color.darkGray);
                }
            }
        }

        for (Creature c : world.creatures()) {
            if ((c.x >= left && c.x < left + screenWidth)
                    && (c.y >= top && c.y < top + screenHeight && c.z == player.z)
                    && player.canSee(c.x, c.y, c.z)) {
                terminal.write(c.glyph(), c.x - left, c.y - top, c.color());
            }
        }
    }

    private void displayMessages(AsciiPanel terminal, List<String> messages) {
        int top = screenHeight - messages.size();
        for (int i = 0; i < messages.size(); i++) {
            terminal.writeCenter(messages.get(i), top + i);
        }
        messages.clear();
    }

    private boolean userIsTryingToExit() {
        return player.z == 0 && world.tile(player.x, player.y, player.z) == Tile.STAIRS_UP;
    }

    private Screen userExits() {
        for (Item item : player.inventory().getItems()) {
            if (item != null && item.name().equals("teddy bear"))
                return new WinScreen();
        }
        return new LoseScreen();
    }

    private String hunger() {
        if (player.food() < player.maxFood() * 0.1)
            return "Starving";
        else if (player.food() < player.maxFood() * 0.2)
            return "Hungry";
        else if (player.food() > player.maxFood() * 0.9)
            return "Stuffed";
        else if (player.food() > player.maxFood() * 0.8)
            return "Full";
        else
            return "";
    }
}
