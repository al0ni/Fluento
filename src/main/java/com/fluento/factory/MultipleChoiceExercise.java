package com.fluento.factory;

import com.fluento.factory.model.*;
import java.util.*;

public final class MultipleChoiceExercise implements Exercise {
    private final Word word;
    private final List<String> options;

    public MultipleChoiceExercise(Word word, List<String> options) {
        this.word = ExerciseSupport.requireWord(word);
        Objects.requireNonNull(options, "options");
        if (options.size() < 2) {
            throw new IllegalArgumentException("At least two options are required");
        }
        List<String> cleaned = options.stream()
                .map(option -> ExerciseSupport.requireText(option, "option")).toList();
        Set<String> unique = new HashSet<>();
        for (String option : cleaned) {
            if (!unique.add(ExerciseSupport.normalize(option))) {
                throw new IllegalArgumentException("Options must be distinct");
            }
        }
        if (!unique.contains(ExerciseSupport.normalize(word.english()))) {
            throw new IllegalArgumentException("Options must contain the correct word");
        }
        this.options = List.copyOf(cleaned);
    }

    @Override
    public String id() {
        return "choice-" + word.id();
    }

    @Override
    public ExerciseView view() {
        return new ExerciseView("Choose the English word for: " + word.translation(),
                options, Optional.empty());
    }

    @Override
    public AnswerResult checkAnswer(String answer) {
        boolean correct = ExerciseSupport.normalize(word.english())
                .equals(ExerciseSupport.normalize(answer));
        return new AnswerResult(correct,
                correct ? "Correct!" : "Correct answer: " + word.english());
    }
}
