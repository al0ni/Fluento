package com.fluento.model;

public interface Exercise {
    String id();

    ExerciseView view();

    AnswerResult checkAnswer(String answer);
}
