package com.fluento.lessons.factory;

import com.fluento.model.Word;
import java.util.Locale;
import java.util.Objects;

final class ExerciseSupport {
    private ExerciseSupport() {}

    static Word requireWord(Word word) {
        Objects.requireNonNull(word, "word");
        requireText(word.id(), "word.id");
        requireText(word.english(), "word.english");
        requireText(word.translation(), "word.translation");
        return word;
    }

    static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value.strip();
    }

    static String normalize(String value) {
        return value == null ? "" : value.strip().toLowerCase(Locale.ROOT);
    }
}
