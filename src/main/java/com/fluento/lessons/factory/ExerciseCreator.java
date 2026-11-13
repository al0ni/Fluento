package com.fluento.lessons.factory;

import com.fluento.model.Exercise;
import com.fluento.model.Word;
import java.util.List;
import java.util.Objects;

public abstract class ExerciseCreator {
    public abstract Exercise createExercise(Word word);

    public final List<Exercise> createExercises(List<Word> words) {
        Objects.requireNonNull(words, "words");
        return words.stream().map(this::createExercise).toList();
    }
}
