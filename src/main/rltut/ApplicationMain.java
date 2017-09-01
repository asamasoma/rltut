package rltut;

import javax.swing.*;

import asciiPanel.AsciiPanel;

public class ApplicationMain extends JFrame {
    private AsciiPanel terminal;

    public ApplicationMain() {
        super();
        terminal = new AsciiPanel();
        terminal.write("rl tutorial", 1, 1);
        add(terminal);
        pack();
    }

    public static void main(String[] args) {
        ApplicationMain app = new ApplicationMain();
        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        app.setVisible(true);
    }
}
