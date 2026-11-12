package com.fluento.model;

import java.util.List;

public record Lesson(
        String id,
        String title,
        List<Exercise> exercises
) {
    public Lesson {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "A lesson must have an identifier"
            );
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "A lesson should have a title"
            );
        }

        exercises = List.copyOf(exercises);

        if (exercises.isEmpty()) {
            throw new IllegalArgumentException(
                    "The lesson should include at least one exercise"
            );
        }
    }
}