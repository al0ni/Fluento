package com.fluento;

import com.fluento.facade.ui.*;

import javax.swing.*;
import java.awt.Dimension;

public final class Main {
    private Main() {}
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Theme.install();
            JFrame frame = new JFrame("Fluento | English, one step at a time");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setContentPane(new FluentoPanel());
            frame.pack();
            frame.setMinimumSize(new Dimension(1000,740));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
