package com.fluento.builder.catalog;

import com.fluento.facade.ui.LanguageLessonsPanel;
import com.fluento.facade.ui.LessonsPanel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;

class SourceLanguageTest {
    @ParameterizedTest
    @EnumSource(SourceLanguage.class)
    void allLanguagesProvideCompleteLessonsAndEnglishAnswers(SourceLanguage language) {
        var catalog = new DemoLessonCatalog(language);
        assertEquals(112, language.loadVocabulary().size());
        for (var lesson : catalog.findAll()) {
            for (var word : catalog.wordsForLesson(lesson.id())) {
                assertEquals(language.loadVocabulary().getProperty(word.id()), word.translation());
                for (var exercise : lesson.exercises()) {
                    if (exercise.id().endsWith("-" + word.id())) {
                        assertTrue(exercise.view().prompt().contains(word.translation()));
                        assertTrue(exercise.checkAnswer(word.english()).correct());
                        assertFalse(exercise.checkAnswer(word.translation()).correct());
                        assertTrue(exercise.view().options().stream()
                                .allMatch(option -> option.matches("[a-z]+")));
                    }
                }
            }
        }
    }

    @Test
    void resourceFilesPreserveRussianAndKazakhCharacters() {
        assertEquals("\u044f\u0431\u043b\u043e\u043a\u043e",
                SourceLanguage.RUSSIAN.loadVocabulary().getProperty("apple"));
        assertEquals("\u0441\u04d9\u043b\u0435\u043c",
                SourceLanguage.KAZAKH.loadVocabulary().getProperty("hello"));
    }

    @Test
    void exerciseIdsStayStableAcrossSourceLanguages() {
        var russian = new DemoLessonCatalog(SourceLanguage.RUSSIAN);
        var kazakh = new DemoLessonCatalog(SourceLanguage.KAZAKH);
        for (var lesson : russian.findAll()) {
            assertEquals(lesson.exercises().stream().map(e -> e.id()).toList(),
                    kazakh.findById(lesson.id()).orElseThrow().exercises().stream()
                            .map(e -> e.id()).toList());
        }
    }

    @Test
    void selectorPassesNewLanguageCatalogToLessonCallback() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            var selected = new AtomicReference<DemoLessonCatalog>();
            var panel = new LanguageLessonsPanel(SourceLanguage.RUSSIAN,
                    (catalog, lesson) -> selected.set(catalog));
            JComboBox<?> selector = find(panel, JComboBox.class);
            selector.setSelectedItem(SourceLanguage.KAZAKH);
            find(find(panel, LessonsPanel.class), JButton.class).doClick();
            assertEquals(SourceLanguage.KAZAKH, selected.get().sourceLanguage());
            selector.setSelectedItem(SourceLanguage.ENGLISH);
            find(find(panel, LessonsPanel.class), JButton.class).doClick();
            assertEquals(SourceLanguage.ENGLISH, selected.get().sourceLanguage());
        });
    }

    private <T extends Component> T find(Container root, Class<T> type) {
        for (Component child : root.getComponents()) {
            if (type.isInstance(child)) return type.cast(child);
            if (child instanceof Container container) {
                T found = find(container, type);
                if (found != null) return found;
            }
        }
        return null;
    }
}
