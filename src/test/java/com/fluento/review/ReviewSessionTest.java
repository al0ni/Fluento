package com.fluento.review;

import com.fluento.lessons.factory.TranslationExercise;
import com.fluento.model.Exercise;
import com.fluento.model.Word;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReviewSessionTest {
    private Exercise exercise(String id) {
        return new TranslationExercise(new Word(id, id, "meaning"));
    }

    @Test
    void usesSelectedStrategy() {
        Exercise a = exercise("a");
        Exercise b = exercise("b");
        ReviewSession session = new ReviewSession(List.of(a, b), new SequentialReviewStrategy());

        assertEquals(List.of(a, b), session.orderedExercises());
    }

    @Test
    void strategyCanBeReplacedAtRuntime() {
        Exercise a = exercise("a");
        Exercise b = exercise("b");
        Map<String, Integer> mistakes = Map.of(b.id(), 3);
        ReviewSession session = new ReviewSession(List.of(a, b), new SequentialReviewStrategy());

        assertEquals(List.of(a, b), session.orderedExercises(mistakes));

        session.setStrategy(new WeakWordsFirstStrategy());

        assertEquals("Weak Words First", session.strategy().name());
        assertEquals(List.of(b, a), session.orderedExercises(mistakes));
    }

    @Test
    void doesNotDependOnLaterChangesOfTheSuppliedList() {
        Exercise a = exercise("a");
        var supplied = new java.util.ArrayList<>(List.of(a));
        ReviewSession session = new ReviewSession(supplied, new SequentialReviewStrategy());

        supplied.add(exercise("b"));

        assertEquals(List.of(a), session.orderedExercises());
    }

    @Test
    void rejectsNulls() {
        assertThrows(NullPointerException.class,
                () -> new ReviewSession(null, new SequentialReviewStrategy()));
        assertThrows(NullPointerException.class,
                () -> new ReviewSession(List.of(), null));
        ReviewSession session = new ReviewSession(List.of(), new SequentialReviewStrategy());
        assertThrows(NullPointerException.class, () -> session.setStrategy(null));
    }
}
