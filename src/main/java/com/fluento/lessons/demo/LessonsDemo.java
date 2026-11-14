package com.fluento.lessons.demo;

import com.fluento.lessons.catalog.*;
import com.fluento.lessons.ui.*;
import com.fluento.model.Lesson;
import com.fluento.session.HintDecorator;
import com.fluento.session.LessonFacade;
import com.fluento.session.LessonSessionPanel;
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
            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Flashcards", new FlashcardsPanel(catalog.wordsForLesson(lesson.id())));
            Lesson practice = new Lesson(lesson.id(), lesson.title(), lesson.exercises().stream()
                    .map(exercise -> exercise.view().hint().isPresent()
                            ? exercise
                            : new HintDecorator(exercise, "Review the words in the Flashcards tab."))
                    .toList());
            tabs.addTab("Practice", new LessonSessionPanel(new LessonFacade(practice)));
            page.add(tabs, BorderLayout.CENTER);
            frame.setContentPane(page);
            frame.revalidate();
            frame.repaint();
        }));
        frame.revalidate();
        frame.repaint();
    }
}
