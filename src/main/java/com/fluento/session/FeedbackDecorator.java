package com.fluento.session;

import com.fluento.model.AnswerResult;
import com.fluento.model.Exercise;
import com.fluento.model.ExerciseView;
import java.util.Objects;


public final class FeedbackDecorator implements Exercise {
    private final Exercise exercise;

    public FeedbackDecorator(Exercise exercise) {
        this.exercise = Objects.requireNonNull(exercise, "exercise");
    }

    @Override public String id() { return exercise.id(); }
    @Override public ExerciseView view() { return exercise.view(); }
    @Override public AnswerResult checkAnswer(String answer) {
        AnswerResult result = exercise.checkAnswer(answer);
        String guidance = result.correct() ? "Next exercise is ready."
                : "Review this word in Weak Words First.";
        return new AnswerResult(result.correct(), result.feedback() + "\n\n" + guidance);
    }
}
