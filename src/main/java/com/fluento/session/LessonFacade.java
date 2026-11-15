package com.fluento.session;

import com.fluento.model.AnswerResult;
import com.fluento.model.Exercise;
import com.fluento.model.ExerciseView;
import com.fluento.model.Lesson;
import java.util.Objects;
import java.util.List;
import com.fluento.progress.ProgressTracker;
import com.fluento.progress.ProgressSnapshot;
import com.fluento.review.ReviewSession;
import com.fluento.review.ReviewStrategy;
import com.fluento.review.SequentialReviewStrategy;

public class LessonFacade {
    public interface AnswerListener {
        void onAnswer(String lessonId, String exerciseId, AnswerResult result);
    }

    private final Lesson lesson;
    private final AnswerListener listener;
    private final ProgressTracker tracker;
    private final List<Exercise> exercises;
    private int exerciseIndex;
    private int score;
    private AnswerResult currentResult;

    public LessonFacade(Lesson lesson) {
        this(lesson, new SequentialReviewStrategy(), new ProgressTracker(), null);
    }

    public LessonFacade(Lesson lesson, AnswerListener listener) {
        this(lesson, new SequentialReviewStrategy(), new ProgressTracker(), listener);
    }

    public LessonFacade(Lesson lesson, ReviewStrategy strategy, ProgressTracker tracker) {
        this(lesson, strategy, tracker, null);
    }

    public LessonFacade(Lesson lesson, ReviewStrategy strategy, ProgressTracker tracker, AnswerListener listener) {
        this.lesson = Objects.requireNonNull(lesson, "Lesson is required");
        this.listener = listener;
        this.tracker = Objects.requireNonNull(tracker, "tracker");
        this.exercises = new ReviewSession(lesson.exercises(), strategy)
                .orderedExercises(tracker.snapshot().mistakesByExerciseId());
    }

    public ExerciseView currentExercise() {
        return activeExercise().view();
    }

    public AnswerResult submitAnswer(String answer) {
        Exercise exercise = activeExercise();
        if (hasAnsweredCurrentExercise()) {
            throw new IllegalStateException("This exercise has already been answered");
        }

        if (answer == null || answer.isBlank()) {
            throw new IllegalArgumentException("Enter or select an answer.");
        }

        AnswerResult result = exercise.checkAnswer(answer);
        return recordResult(exercise, result);
    }

    private AnswerResult recordResult(Exercise exercise, AnswerResult result) {
        Objects.requireNonNull(result, "result");
        currentResult = result;
        if (result.correct()) {
            score++;
        }
        tracker.recordAnswer(exercise.id(), result);
        if (listener != null) {
            try {
                listener.onAnswer(lesson.id(), exercise.id(), result);
            } catch (RuntimeException error) {
                System.err.println("Answer listener failed: " + error.getMessage());
            }
        }
        return result;
    }

    
    public AnswerResult skipAnswer() {
        activeExercise();
        if (hasAnsweredCurrentExercise()) throw new IllegalStateException("Already answered");
        return recordResult(activeExercise(), new AnswerResult(false,
                "Skipped.\n\n" + activeExercise().checkAnswer("").feedback()));
    }

    public void restart() {
        exerciseIndex = 0;
        score = 0;
        currentResult = null;
    }

    public void nextExercise() {
        requireActiveLesson();
        if (!hasAnsweredCurrentExercise()) {
            throw new IllegalStateException("Answer the current exercise first");
        }

        exerciseIndex++;
        currentResult = null;
    }

    public boolean isComplete() {
        return exerciseIndex == exerciseCount();
    }

    public int score() {
        return score;
    }

    public int answeredCount() {
        return exerciseIndex + (hasAnsweredCurrentExercise() ? 1 : 0);
    }

    public int exerciseCount() {
        return exercises.size();
    }

    public String currentExerciseId() { return activeExercise().id(); }

    public ProgressSnapshot progress() { return tracker.snapshot(); }

    public String lessonTitle() { return lesson.title(); }

    public boolean hasAnsweredCurrentExercise() {
        return currentResult != null;
    }

    public String currentFeedback() {
        return hasAnsweredCurrentExercise() ? currentResult.feedback() : "";
    }

    private Exercise activeExercise() {
        requireActiveLesson();
        return exercises.get(exerciseIndex);
    }

    private void requireActiveLesson() {
        if (isComplete()) {
            throw new IllegalStateException("This lesson is complete");
        }
    }
}
