package com.fluento.strategy;

import com.fluento.factory.model.Exercise;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class WeakWordsFirstStrategy implements ReviewStrategy {

    @Override
    public String name() {
        return "Weak Words First";
    }

    @Override
    public List<Exercise> order(List<Exercise> exercises, Map<String, Integer> mistakes) {
        Objects.requireNonNull(exercises, "exercises");
        Map<String, Integer> counts = mistakes == null ? Map.of() : Map.copyOf(mistakes);
        if (counts.values().stream().anyMatch(value -> value < 0)) {
            throw new IllegalArgumentException("Mistake counts must not be negative");
        }
        List<Exercise> result = new ArrayList<>(exercises);
        result.sort((first, second) -> Integer.compare(
                counts.getOrDefault(second.id(), 0),
                counts.getOrDefault(first.id(), 0)));
        return List.copyOf(result);
    }
}
