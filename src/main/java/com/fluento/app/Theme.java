package com.fluento.app;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
public final class Theme {
    public static final Color INK = new Color(35,39,57), MUTED = new Color(99,105,126),
        BACKGROUND = new Color(245,246,251), ACCENT = new Color(93,73,200), PALE = new Color(235,231,252);
    private Theme() {}
    public static void install() {
        Font font = new Font("Segoe UI",Font.PLAIN,15);
        for (Object key : java.util.Collections.list(UIManager.getDefaults().keys()))
            if (key.toString().endsWith(".font")) UIManager.put(key,font);
        UIManager.put("Panel.background",BACKGROUND);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0,0,0,0));
        UIManager.put("TabbedPane.selected", Color.WHITE);
        UIManager.put("Button.disabledText", MUTED);
        UIManager.put("Label.foreground",INK);
        UIManager.put("Button.select",PALE);
        UIManager.put("ProgressBar.foreground",ACCENT);
    }
    public static JLabel label(String text,int size,boolean bold) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI",bold ? Font.BOLD : Font.PLAIN,size));
        label.setForeground(INK); return label;
    }
    public static JButton button(String text,boolean primary) {
        JButton b = new JButton(text);
        b.setFocusPainted(true); b.setOpaque(true);
        b.setBackground(primary ? ACCENT : Color.WHITE);
        b.setForeground(primary ? Color.WHITE : INK);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(primary ? ACCENT : new Color(222,224,234)),
            new EmptyBorder(11,18,11,18)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return b;
    }
    public static JPanel card(LayoutManager layout) {
        JPanel p = new JPanel(layout); p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(22,24,22,24)); return p;
    }
}
