package com.fluento.strategy;

import com.fluento.factory.model.Exercise;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class SequentialReviewStrategy implements ReviewStrategy {

    @Override
    public String name() {
        return "Sequential Review";
    }

    @Override
    public List<Exercise> order(List<Exercise> exercises, Map<String, Integer> mistakes) {
        Objects.requireNonNull(exercises, "exercises");
        return List.copyOf(exercises);
    }
}
