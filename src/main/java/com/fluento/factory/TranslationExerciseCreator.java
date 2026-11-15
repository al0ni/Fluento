package com.fluento.factory;

import com.fluento.factory.model.Exercise;
import com.fluento.factory.model.Word;

public final class TranslationExerciseCreator extends ExerciseCreator {
    @Override
    public Exercise createExercise(Word word) {
        return new TranslationExercise(word);
    }
}
