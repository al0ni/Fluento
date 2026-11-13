package com.fluento.lessons.factory;

import com.fluento.model.*;
import java.util.List;
import java.util.Optional;

public final class TranslationExercise implements Exercise {
    private final Word word;

    public TranslationExercise(Word word) {
        this.word = ExerciseSupport.requireWord(word);
    }

    @Override
    public String id() {
        return "translation-" + word.id();
    }

    @Override
    public ExerciseView view() {
        return new ExerciseView("Write the English word for: " + word.translation(),
                List.of(), Optional.empty());
    }

    @Override
    public AnswerResult checkAnswer(String answer) {
        boolean correct = ExerciseSupport.normalize(word.english())
                .equals(ExerciseSupport.normalize(answer));
        return new AnswerResult(correct,
                correct ? "Correct!" : "Correct answer: " + word.english());
    }
}
