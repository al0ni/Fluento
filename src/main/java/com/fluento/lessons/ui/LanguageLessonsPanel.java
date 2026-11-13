package com.fluento.lessons.ui;

import com.fluento.lessons.catalog.*;
import com.fluento.model.Lesson;
import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.function.BiConsumer;

public final class LanguageLessonsPanel extends JPanel {
    public LanguageLessonsPanel(SourceLanguage initialLanguage,
                                BiConsumer<DemoLessonCatalog, Lesson> onSelect) {
        super(new BorderLayout());
        Objects.requireNonNull(initialLanguage, "initialLanguage");
        Objects.requireNonNull(onSelect, "onSelect");
        JComboBox<SourceLanguage> languages = new JComboBox<>(SourceLanguage.values());
        languages.setSelectedItem(initialLanguage);
        JPanel settings = new JPanel(new FlowLayout(FlowLayout.LEFT));
        settings.add(new JLabel("Learn English from:"));
        settings.add(languages);
        add(settings, BorderLayout.NORTH);
        JPanel content = new JPanel(new BorderLayout());
        add(content, BorderLayout.CENTER);
        Runnable refresh = () -> {
            SourceLanguage language = (SourceLanguage) languages.getSelectedItem();
            DemoLessonCatalog catalog = new DemoLessonCatalog(language);
            content.removeAll();
            content.add(new LessonsPanel(catalog, lesson -> onSelect.accept(catalog, lesson)),
                    BorderLayout.CENTER);
            content.revalidate();
            content.repaint();
        };
        languages.addActionListener(event -> refresh.run());
        refresh.run();
    }
}
