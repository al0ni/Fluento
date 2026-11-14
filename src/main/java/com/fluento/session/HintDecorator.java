package com.fluento.session;

import com.fluento.model.AnswerResult;
import com.fluento.model.Exercise;
import com.fluento.model.ExerciseView;
import java.util.Optional;

public class HintDecorator implements Exercise {
    private final Exercise exercise;
    private final String hint;

    public HintDecorator(Exercise exercise, String hint) {
        this.exercise = exercise;
        this.hint = hint;
    }

    @Override
    public String id() {
        return exercise.id();
    }

    @Override
    public ExerciseView view() {
        ExerciseView view = exercise.view();
        return new ExerciseView(view.prompt(), view.options(), Optional.of(hint));
    }

    @Override
    public AnswerResult checkAnswer(String answer) {
        return exercise.checkAnswer(answer);
    }
}
