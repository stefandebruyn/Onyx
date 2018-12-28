package onyx.ui;

import javax.swing.*;

public class OnyxInterface {
    private Display display;

    public OnyxInterface(Display display) {
        this.display = display;
        JFrame frame = new JFrame("Onyx");

        frame.setContentPane(display);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public Display display() {
        return display;
    }
}
