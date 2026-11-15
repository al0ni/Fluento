package com.fluento.factory;

import com.fluento.factory.model.Exercise;
import com.fluento.factory.model.Word;
import java.util.List;
import java.util.Objects;

public abstract class ExerciseCreator {
    public abstract Exercise createExercise(Word word);

    public final List<Exercise> createExercises(List<Word> words) {
        Objects.requireNonNull(words, "words");
        return words.stream().map(this::createExercise).toList();
    }
}
