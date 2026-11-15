package com.fluento.review;

import com.fluento.model.Exercise;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ReviewSession {
    private final List<Exercise> exercises;
    private ReviewStrategy strategy;

    public ReviewSession(List<Exercise> exercises, ReviewStrategy strategy) {
        this.exercises = List.copyOf(Objects.requireNonNull(exercises, "exercises"));
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    public ReviewStrategy strategy() {
        return strategy;
    }

    public void setStrategy(ReviewStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    public List<Exercise> orderedExercises() {
        return strategy.order(exercises, Map.of());
    }

    public List<Exercise> orderedExercises(Map<String, Integer> mistakes) {
        return strategy.order(exercises, mistakes);
    }
}
