package com.fluento.review;

import com.fluento.lessons.catalog.DemoLessonCatalog;
import com.fluento.model.AnswerResult;
import com.fluento.model.Exercise;
import com.fluento.model.Lesson;
import com.fluento.progress.ConsoleProgressObserver;
import com.fluento.progress.ProgressTracker;

import java.io.PrintStream;
import java.util.List;

public final class ReviewProgressDemo {
    private ReviewProgressDemo() {}

    public static void main(String[] args) {
        run(System.out);
    }

    static void run(PrintStream out) {
        Lesson lesson = new DemoLessonCatalog().findById("food").orElseThrow();
        ProgressTracker tracker = new ProgressTracker();
        tracker.subscribe(new ConsoleProgressObserver(out));
        ReviewSession session = new ReviewSession(lesson.exercises(), new SequentialReviewStrategy());

        out.println("Lesson: " + lesson.title());

        out.println("Round 1 - " + session.strategy().name());
        play(session.orderedExercises(tracker.snapshot().mistakesByExerciseId()), tracker, out, true);

        session.setStrategy(new WeakWordsFirstStrategy());

        out.println("Round 2 - " + session.strategy().name());
        play(session.orderedExercises(tracker.snapshot().mistakesByExerciseId()), tracker, out, false);
    }

    private static void play(List<Exercise> exercises, ProgressTracker tracker,
                             PrintStream out, boolean failWater) {
        int number = 1;
        for (Exercise exercise : exercises) {
            boolean fails = failWater && exercise.id().endsWith("water");
            String answer = fails ? "???" : exercise.id().substring(exercise.id().indexOf('-') + 1);
            AnswerResult result = exercise.checkAnswer(answer);
            out.println("  " + number++ + ". " + exercise.id() + " -> "
                    + (result.correct() ? "correct" : "wrong"));
            tracker.recordAnswer(exercise.id(), result);
        }
    }
}
