package com.fluento.progress;

import java.util.Map;

public record ProgressSnapshot(
        int correct,
        int incorrect,
        Map<String, Integer> mistakesByExerciseId
) {
    public ProgressSnapshot {
        if (correct < 0 || incorrect < 0) {
            throw new IllegalArgumentException("Counters must not be negative");
        }
        mistakesByExerciseId = Map.copyOf(mistakesByExerciseId);
        if (mistakesByExerciseId.entrySet().stream().anyMatch(e -> e.getKey().isBlank() || e.getValue() < 0)) {
            throw new IllegalArgumentException("Mistake IDs must not be blank and counts must not be negative");
        }
    }

    public static ProgressSnapshot empty() {
        return new ProgressSnapshot(0, 0, Map.of());
    }

    public int total() {
        return correct + incorrect;
    }

    public double accuracyPercent() {
        return total() == 0 ? 0.0 : correct * 100.0 / total();
    }

    public int mistakesFor(String exerciseId) {
        return mistakesByExerciseId.getOrDefault(exerciseId, 0);
    }
}
