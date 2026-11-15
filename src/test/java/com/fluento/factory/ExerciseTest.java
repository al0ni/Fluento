package com.fluento.factory;

import com.fluento.factory.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ExerciseTest {
    private final Word apple = new Word("apple", "apple", "a round fruit that grows on trees");

    @ParameterizedTest
    @ValueSource(strings = {"apple", "APPLE", "  Apple  "})
    void translationAcceptsCaseAndOuterWhitespace(String answer) {
        assertTrue(new TranslationExercise(apple).checkAnswer(answer).correct());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "pear", "a round fruit that grows on trees"})
    void translationRejectsWrongAnswers(String answer) {
        AnswerResult result = new TranslationExercise(apple).checkAnswer(answer);
        assertFalse(result.correct());
        assertTrue(result.feedback().contains("apple"));
    }

    @Test
    void translationViewHasNoAnswerOrOptions() {
        var view = new TranslationExercise(apple).view();
        assertTrue(view.prompt().contains("a round fruit that grows on trees"));
        assertFalse(view.prompt().contains("apple"));
        assertTrue(view.options().isEmpty());
        assertTrue(view.hint().isEmpty());
    }

    @Test
    void choiceChecksTextNotOptionNumberAndCopiesOptions() {
        var options = new ArrayList<>(List.of("apple", "bread"));
        var exercise = new MultipleChoiceExercise(apple, options);
        options.clear();
        assertEquals(2, exercise.view().options().size());
        assertTrue(exercise.checkAnswer(" APPLE ").correct());
        assertFalse(exercise.checkAnswer("bread").correct());
        assertFalse(exercise.checkAnswer("1").correct());
        assertFalse(exercise.checkAnswer(null).correct());
        assertThrows(UnsupportedOperationException.class,
                () -> exercise.view().options().add("water"));
    }

    @Test
    void malformedOptionsAreRejected() {
        for (var options : List.of(List.of("apple"), List.of("water", "bread"),
                List.of("apple", " APPLE "), List.of("apple", " "))) {
            assertThrows(IllegalArgumentException.class,
                    () -> new MultipleChoiceExercise(apple, options));
        }
    }

    @Test
    void invalidWordsAreRejectedAtExerciseBoundary() {
        assertThrows(NullPointerException.class, () -> new TranslationExercise(null));
        for (var word : List.of(new Word("", "apple", "apple"),
                new Word("a", null, "apple"), new Word("a", "apple", " "))) {
            assertThrows(IllegalArgumentException.class, () -> new TranslationExercise(word));
            assertThrows(IllegalArgumentException.class,
                    () -> new MultipleChoiceExercise(word, List.of("apple", "bread")));
        }
    }

    @Test
    void factoryWorkflowCreatesDifferentProducts() {
        List<Word> words = List.of(apple, new Word("bread", "bread", "bread"));
        ExerciseCreator translations = new TranslationExerciseCreator();
        ExerciseCreator choices = new MultipleChoiceExerciseCreator(words);
        assertTrue(translations.createExercises(words).stream()
                .allMatch(TranslationExercise.class::isInstance));
        assertTrue(choices.createExercises(words).stream()
                .allMatch(MultipleChoiceExercise.class::isInstance));
        assertNotEquals(translations.createExercise(apple).id(), choices.createExercise(apple).id());
    }

    @Test
    void choiceFactoryDeduplicatesDistractorsAndSupportsExternalWords() {
        var creator = new MultipleChoiceExerciseCreator(List.of(apple,
                new Word("apple2", " APPLE ", "a round fruit"),
                new Word("bread", "bread", "bread")));
        var external = creator.createExercise(new Word("water", "water", "water"));
        assertEquals(3, external.view().options().size());
        assertTrue(external.checkAnswer("water").correct());
        assertEquals(external.view(), creator.createExercise(new Word("water", "water", "water")).view());
    }

    @Test
    void choiceFactoryRequiresRealDistractors() {
        assertThrows(IllegalArgumentException.class,
                () -> new MultipleChoiceExerciseCreator(List.of(apple, apple)));
    }
}
