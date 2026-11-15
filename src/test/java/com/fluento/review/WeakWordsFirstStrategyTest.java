package com.fluento.review;

import com.fluento.lessons.factory.TranslationExercise;
import com.fluento.model.Exercise;
import com.fluento.model.Word;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WeakWordsFirstStrategyTest {
    private Exercise exercise(String id) {
        return new TranslationExercise(new Word(id, id, "meaning"));
    }

    @Test
    void movesDifficultExerciseToTheFront() {
        Exercise apple = exercise("apple");
        Exercise house = exercise("house");
        Exercise water = exercise("water");
        Exercise school = exercise("school");

        List<Exercise> result = new WeakWordsFirstStrategy()
                .order(List.of(apple, house, water, school), Map.of(water.id(), 3));

        assertEquals(List.of(water, apple, house, school), result);
    }

    @Test
    void sortsByNumberOfMistakesDescending() {
        Exercise apple = exercise("apple");
        Exercise house = exercise("house");
        Exercise water = exercise("water");

        List<Exercise> result = new WeakWordsFirstStrategy().order(
                List.of(apple, house, water),
                Map.of(apple.id(), 1, house.id(), 4, water.id(), 2));

        assertEquals(List.of(house, water, apple), result);
    }

    @Test
    void keepsOriginalOrderForEqualMistakes() {
        Exercise a = exercise("a");
        Exercise b = exercise("b");
        Exercise c = exercise("c");

        List<Exercise> result = new WeakWordsFirstStrategy()
                .order(List.of(a, b, c), Map.of(b.id(), 2, c.id(), 2));

        assertEquals(List.of(b, c, a), result);
    }

    @Test
    void withoutMistakesBehavesLikeSequential() {
        List<Exercise> input = List.of(exercise("a"), exercise("b"), exercise("c"));

        assertEquals(input, new WeakWordsFirstStrategy().order(input, Map.of()));
        assertEquals(input, new WeakWordsFirstStrategy().order(input, null));
    }

    @Test
    void doesNotChangeOriginalList() {
        Exercise a = exercise("a");
        Exercise b = exercise("b");
        List<Exercise> input = new ArrayList<>(List.of(a, b));

        List<Exercise> result = new WeakWordsFirstStrategy().order(input, Map.of(b.id(), 2));

        assertEquals(List.of(a, b), input);
        assertEquals(List.of(b, a), result);
        assertThrows(UnsupportedOperationException.class, result::clear);
    }
}
