package com.fluento.lessons.builder;

import com.fluento.lessons.factory.TranslationExercise;
import com.fluento.model.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class LessonBuilderTest {
    private Exercise exercise(String id) {
        return new TranslationExercise(new Word(id, id, "meaning"));
    }

    @Test
    void buildsImmutableSnapshotInInsertionOrder() {
        Exercise first = exercise("one");
        Exercise second = exercise("two");
        var supplied = new ArrayList<>(List.of(first));
        var builder = new LessonBuilder().id(" basics ").title(" Basics ").addExercises(supplied);
        Lesson lesson = builder.build();
        supplied.clear();
        builder.addExercise(second);
        assertEquals("basics", lesson.id());
        assertEquals("Basics", lesson.title());
        assertEquals(List.of(first), lesson.exercises());
        assertEquals(List.of(first, second), builder.build().exercises());
        assertThrows(UnsupportedOperationException.class, () -> lesson.exercises().clear());
    }

    @Test
    void rejectsMissingIdTitleOrExercises() {
        assertThrows(IllegalArgumentException.class,
                () -> new LessonBuilder().title("Basics").addExercise(exercise("one")).build());
        assertThrows(IllegalArgumentException.class,
                () -> new LessonBuilder().id("basics").addExercise(exercise("one")).build());
        assertThrows(IllegalArgumentException.class,
                () -> new LessonBuilder().id("basics").title("Basics").build());
        assertThrows(IllegalArgumentException.class,
                () -> new LessonBuilder().id(" ").title("Basics").addExercise(exercise("one")).build());
    }

    @Test
    void rejectsDuplicateIdsAndNullExercises() {
        assertThrows(IllegalArgumentException.class,
                () -> new LessonBuilder().id("a").title("A")
                        .addExercise(exercise("same")).addExercise(exercise("same")).build());
        assertThrows(NullPointerException.class, () -> new LessonBuilder().addExercise(null));
    }
}
