package rltut;

import asciiPanel.AsciiPanel;
import rltut.screens.Screen;
import rltut.screens.StartScreen;

import java.applet.Applet;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class AppletMain extends Applet implements KeyListener {
    private AsciiPanel terminal;
    private Screen screen;

    public AppletMain() {
        super();
        terminal = new AsciiPanel();
        add(terminal);
        screen = new StartScreen();
        addKeyListener(this);
        repaint();
    }

    public void init() {
        super.init();
        this.setSize(terminal.getWidth() + 20, terminal.getHeight() + 20);
    }

    public void repaint() {
        terminal.clear();
        screen.displayOutput(terminal);
        super.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        screen = screen.respondToUserInput(e);
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
}
