package com.fluento.lessons.factory;

import com.fluento.model.Exercise;
import com.fluento.model.Word;

public final class TranslationExerciseCreator extends ExerciseCreator {
    @Override
    public Exercise createExercise(Word word) {
        return new TranslationExercise(word);
    }
}
