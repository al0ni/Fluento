package com.fluento.session;

import com.fluento.model.AnswerResult;
import com.fluento.model.Exercise;
import com.fluento.model.ExerciseView;
import com.fluento.model.Lesson;
import java.util.Objects;

public class LessonFacade {
    public interface AnswerListener {
        void onAnswer(String lessonId, String exerciseId, AnswerResult result);
    }

    private final Lesson lesson;
    private final AnswerListener listener;
    private int exerciseIndex;
    private int score;
    private AnswerResult currentResult;

    public LessonFacade(Lesson lesson) {
        this(lesson, null);
    }

    public LessonFacade(Lesson lesson, AnswerListener listener) {
        this.lesson = Objects.requireNonNull(lesson, "Lesson is required");
        this.listener = listener;
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
        currentResult = result;
        if (result.correct()) {
            score++;
        }
        if (listener != null) {
            listener.onAnswer(lesson.id(), exercise.id(), result);
        }
        return result;
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
        return lesson.exercises().size();
    }

    public boolean hasAnsweredCurrentExercise() {
        return currentResult != null;
    }

    public String currentFeedback() {
        return hasAnsweredCurrentExercise() ? currentResult.feedback() : "";
    }

    private Exercise activeExercise() {
        requireActiveLesson();
        return lesson.exercises().get(exerciseIndex);
    }

    private void requireActiveLesson() {
        if (isComplete()) {
            throw new IllegalStateException("This lesson is complete");
        }
    }
}
