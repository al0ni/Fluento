package com.fluento.lessons.demo;

import com.fluento.lessons.catalog.*;
import com.fluento.lessons.ui.*;
import javax.swing.*;
import java.awt.*;

public final class LessonsDemo {
    private LessonsDemo() {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Fluento - Lessons and Flashcards");
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setMinimumSize(new Dimension(640, 400));
            frame.setSize(760, 480);
            showLessons(frame, SourceLanguage.RUSSIAN);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static void showLessons(JFrame frame, SourceLanguage language) {
        frame.setContentPane(new LanguageLessonsPanel(language, (catalog, lesson) -> {
            JPanel page = new JPanel(new BorderLayout());
            JButton back = new JButton("All lessons");
            back.addActionListener(event -> showLessons(frame, catalog.sourceLanguage()));
            page.add(back, BorderLayout.NORTH);
            page.add(new FlashcardsPanel(catalog.wordsForLesson(lesson.id())),
                    BorderLayout.CENTER);
            frame.setContentPane(page);
            frame.revalidate();
            frame.repaint();
        }));
        frame.revalidate();
        frame.repaint();
    }
}
