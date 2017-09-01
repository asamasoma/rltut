package rltut;

import asciiPanel.AsciiPanel;

import java.applet.Applet;

public class AppletMain extends Applet {
    private AsciiPanel terminal;

    public AppletMain() {
        super();
        terminal = new AsciiPanel();
        terminal.write("rl tutorial", 1,1);
        add(terminal);
    }

    public void init() {
        super.init();
        this.setSize(terminal.getWidth() + 20, terminal.getHeight() + 20);
    }

    public void repaint() {
        super.repaint();
        terminal.repaint();
    }
}
