package com.fluento.builder.catalog;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.*;

class DemoLessonCatalogTest {
    @Test
    void allLessonsConnectFactoriesBuilderAndVocabulary() {
        LessonCatalog catalog = new DemoLessonCatalog();
        assertEquals(16, catalog.findAll().size());
        var ids = new HashSet<String>();
        for (var lesson : catalog.findAll()) {
            assertSame(lesson, catalog.findById(lesson.id()).orElseThrow());
            var words = catalog.wordsForLesson(lesson.id());
            assertEquals(words.size()*2, lesson.exercises().size());
            assertTrue(words.size() == 4 || words.size() == 8);
            for (var exercise : lesson.exercises()) {
                assertTrue(ids.add(exercise.id()), "Exercise ids should be globally unique in demo");
                var word = words.stream()
                        .filter(w -> exercise.id().endsWith("-" + w.id())).findFirst().orElseThrow();
                assertTrue(exercise.checkAnswer(word.english()).correct());
                assertFalse(exercise.checkAnswer("incorrect-answer").correct());
                if (!exercise.view().options().isEmpty()) {
                    assertEquals(4, exercise.view().options().size());
                    assertTrue(exercise.view().options().contains(word.english()));
                }
            }
        }
    }

    @Test
    void unknownLessonsAndReadOnlyCollectionsHaveExplicitBehavior() {
        var catalog = new DemoLessonCatalog();
        assertTrue(catalog.findById("unknown").isEmpty());
        assertThrows(IllegalArgumentException.class, () -> catalog.wordsForLesson("unknown"));
        assertThrows(UnsupportedOperationException.class, () -> catalog.findAll().clear());
        assertThrows(UnsupportedOperationException.class, () -> catalog.wordsForLesson("food").clear());
    }
}
