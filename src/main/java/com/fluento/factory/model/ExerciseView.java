package com.fluento.factory.model;

import java.util.List;
import java.util.Optional;

public record ExerciseView(
        String prompt,
        List<String> options,
        Optional<String> hint
) {
    public ExerciseView {
        options = List.copyOf(options);
    }
}
