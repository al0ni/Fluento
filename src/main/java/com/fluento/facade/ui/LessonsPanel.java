package com.fluento.facade.ui;

import com.fluento.builder.catalog.LessonCatalog;
import com.fluento.factory.model.Lesson;
import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.function.Consumer;

public final class LessonsPanel extends JPanel {
    public LessonsPanel(LessonCatalog catalog, Consumer<Lesson> onSelect) {
        super(new BorderLayout(12, 12));
        Objects.requireNonNull(catalog, "catalog");
        Objects.requireNonNull(onSelect, "onSelect");
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel heading = new JLabel("Fluento - Choose a lesson");
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 22f));
        add(heading, BorderLayout.NORTH);
        JPanel buttons = new JPanel(new GridLayout(0, 1, 10, 10));
        for (Lesson lesson : catalog.findAll()) {
            JButton button = new JButton(lesson.title() + " · "
                    + lesson.exercises().size() + " exercises");
            button.addActionListener(event -> onSelect.accept(lesson));
            buttons.add(button);
        }
        if (catalog.findAll().isEmpty()) {
            buttons.add(new JLabel("No lessons available yet"));
        }
        add(buttons, BorderLayout.CENTER);
    }
}
