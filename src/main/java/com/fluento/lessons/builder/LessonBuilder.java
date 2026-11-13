package com.fluento.lessons.builder;

import com.fluento.model.Exercise;
import com.fluento.model.Lesson;
import java.util.*;

public final class LessonBuilder {
    private String id;
    private String title;
    private final List<Exercise> exercises = new ArrayList<>();

    public LessonBuilder id(String id) {
        this.id = id;
        return this;
    }

    public LessonBuilder title(String title) {
        this.title = title;
        return this;
    }

    public LessonBuilder addExercise(Exercise exercise) {
        exercises.add(Objects.requireNonNull(exercise, "exercise"));
        return this;
    }

    public LessonBuilder addExercises(List<Exercise> exercises) {
        this.exercises.addAll(List.copyOf(exercises));
        return this;
    }

    public Lesson build() {
        Set<String> ids = new HashSet<>();
        for (Exercise exercise : exercises) {
            if (exercise.id() == null || exercise.id().isBlank()) {
                throw new IllegalArgumentException("Exercise id must not be blank");
            }
            if (!ids.add(exercise.id())) {
                throw new IllegalArgumentException("Duplicate exercise id: " + exercise.id());
            }
        }
        return new Lesson(id == null ? null : id.strip(),
                title == null ? null : title.strip(), exercises);
    }
}
