package com.fluento.lessons.ui;

import com.fluento.model.Word;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public final class FlashcardsPanel extends JPanel {
    private final List<Word> words;
    private int index;
    private boolean revealed;
    private final JLabel counter = new JLabel("", SwingConstants.CENTER);
    private final JLabel word = new JLabel("", SwingConstants.CENTER);
    private final JLabel translation = new JLabel("", SwingConstants.CENTER);
    private final JButton previous = new JButton("Previous");
    private final JButton reveal = new JButton("Show meaning");
    private final JButton next = new JButton("Next");

    public FlashcardsPanel(List<Word> words) {
        super(new BorderLayout(12, 12));
        this.words = List.copyOf(words);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        word.setFont(word.getFont().deriveFont(Font.BOLD, 30f));
        translation.setFont(translation.getFont().deriveFont(22f));
        JPanel card = new JPanel(new GridLayout(2, 1, 10, 10));
        card.add(word);
        card.add(translation);
        add(counter, BorderLayout.NORTH);
        add(card, BorderLayout.CENTER);
        JPanel controls = new JPanel();
        controls.add(previous);
        controls.add(reveal);
        controls.add(next);
        add(controls, BorderLayout.SOUTH);
        previous.addActionListener(event -> move(-1));
        next.addActionListener(event -> move(1));
        reveal.addActionListener(event -> {
            revealed = !revealed;
            refresh();
        });
        refresh();
    }

    private void move(int delta) {
        int target = index + delta;
        if (target >= 0 && target < words.size()) {
            index = target;
            revealed = false;
            refresh();
        }
    }

    private void refresh() {
        boolean empty = words.isEmpty();
        counter.setText(empty ? "No flashcards" : (index + 1) + " / " + words.size());
        word.setText(empty ? "Add words to this lesson" : words.get(index).english());
        translation.setText(!empty && revealed ? words.get(index).translation() : " ");
        reveal.setText(revealed ? "Hide meaning" : "Show meaning");
        reveal.setEnabled(!empty);
        previous.setEnabled(!empty && index > 0);
        next.setEnabled(!empty && index < words.size() - 1);
    }
}
