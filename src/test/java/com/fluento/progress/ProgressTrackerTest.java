package com.fluento.progress;

import com.fluento.model.AnswerResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProgressTrackerTest {
    private static final AnswerResult RIGHT = new AnswerResult(true, "Correct!");
    private static final AnswerResult WRONG = new AnswerResult(false, "Wrong");

    @Test
    void emptyTrackerHasZeroStatistics() {
        ProgressSnapshot snapshot = new ProgressTracker().snapshot();

        assertEquals(0, snapshot.correct());
        assertEquals(0, snapshot.incorrect());
        assertEquals(0, snapshot.total());
        assertEquals(0.0, snapshot.accuracyPercent());
        assertTrue(snapshot.mistakesByExerciseId().isEmpty());
    }

    @Test
    void correctAnswersIncreaseCorrectCounter() {
        ProgressTracker tracker = new ProgressTracker();

        tracker.recordAnswer("e1", RIGHT);
        tracker.recordAnswer("e2", RIGHT);

        assertEquals(2, tracker.snapshot().correct());
        assertEquals(0, tracker.snapshot().incorrect());
    }

    @Test
    void incorrectAnswersAreCounted() {
        ProgressTracker tracker = new ProgressTracker();

        tracker.recordAnswer("e1", WRONG);

        assertEquals(0, tracker.snapshot().correct());
        assertEquals(1, tracker.snapshot().incorrect());
    }

    @Test
    void fiveAnswersWithFourCorrectGiveEightyPercent() {
        ProgressTracker tracker = new ProgressTracker();

        tracker.recordAnswer("e1", RIGHT);
        tracker.recordAnswer("e2", RIGHT);
        tracker.recordAnswer("e3", WRONG);
        tracker.recordAnswer("e4", RIGHT);
        tracker.recordAnswer("e5", RIGHT);

        ProgressSnapshot snapshot = tracker.snapshot();
        assertEquals(4, snapshot.correct());
        assertEquals(1, snapshot.incorrect());
        assertEquals(5, snapshot.total());
        assertEquals(80.0, snapshot.accuracyPercent(), 0.0001);
    }

    @Test
    void mistakesAreTrackedPerExerciseId() {
        ProgressTracker tracker = new ProgressTracker();

        tracker.recordAnswer("water", WRONG);
        tracker.recordAnswer("water", WRONG);
        tracker.recordAnswer("water", WRONG);
        tracker.recordAnswer("apple", WRONG);
        tracker.recordAnswer("house", RIGHT);

        ProgressSnapshot snapshot = tracker.snapshot();
        assertEquals(3, snapshot.mistakesFor("water"));
        assertEquals(1, snapshot.mistakesFor("apple"));
        assertEquals(0, snapshot.mistakesFor("house"));
        assertEquals(0, snapshot.mistakesFor("unknown"));
    }

    @Test
    void snapshotIsNotAffectedByLaterAnswers() {
        ProgressTracker tracker = new ProgressTracker();
        tracker.recordAnswer("e1", WRONG);
        ProgressSnapshot before = tracker.snapshot();

        tracker.recordAnswer("e1", WRONG);
        tracker.recordAnswer("e2", RIGHT);

        assertEquals(1, before.incorrect());
        assertEquals(0, before.correct());
        assertEquals(1, before.mistakesFor("e1"));
        assertThrows(UnsupportedOperationException.class,
                () -> before.mistakesByExerciseId().put("x", 1));
    }

    @Test
    void rejectsInvalidInput() {
        ProgressTracker tracker = new ProgressTracker();

        assertThrows(IllegalArgumentException.class, () -> tracker.recordAnswer(null, RIGHT));
        assertThrows(IllegalArgumentException.class, () -> tracker.recordAnswer(" ", RIGHT));
        assertThrows(NullPointerException.class, () -> tracker.recordAnswer("e1", null));
        assertEquals(0, tracker.snapshot().total());
    }
}
