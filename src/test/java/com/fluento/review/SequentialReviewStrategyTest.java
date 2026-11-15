package com.fluento.review;

import com.fluento.lessons.factory.TranslationExercise;
import com.fluento.model.Exercise;
import com.fluento.model.Word;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SequentialReviewStrategyTest {
    private Exercise exercise(String id) {
        return new TranslationExercise(new Word(id, id, "meaning"));
    }

    @Test
    void keepsOriginalOrder() {
        Exercise apple = exercise("apple");
        Exercise house = exercise("house");
        Exercise water = exercise("water");
        Exercise school = exercise("school");
        List<Exercise> input = List.of(apple, house, water, school);

        List<Exercise> result = new SequentialReviewStrategy().order(input, Map.of());

        assertEquals(List.of(apple, house, water, school), result);
    }

    @Test
    void ignoresMistakes() {
        Exercise apple = exercise("apple");
        Exercise water = exercise("water");

        List<Exercise> result = new SequentialReviewStrategy()
                .order(List.of(apple, water), Map.of(water.id(), 5));

        assertEquals(List.of(apple, water), result);
    }

    @Test
    void doesNotChangeOriginalListAndReturnsImmutableCopy() {
        List<Exercise> input = new ArrayList<>(List.of(exercise("a"), exercise("b")));
        List<Exercise> before = List.copyOf(input);

        List<Exercise> result = new SequentialReviewStrategy().order(input, Map.of());
        input.clear();

        assertEquals(before, result);
        assertThrows(UnsupportedOperationException.class, result::clear);
    }

    @Test
    void rejectsNullExercises() {
        assertThrows(NullPointerException.class,
                () -> new SequentialReviewStrategy().order(null, Map.of()));
    }
}
