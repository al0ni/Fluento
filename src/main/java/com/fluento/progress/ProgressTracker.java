package com.fluento.progress;

import com.fluento.model.AnswerResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ProgressTracker {
    private final List<ProgressObserver> observers = new ArrayList<>();
    private final Map<String, Integer> mistakesByExerciseId = new LinkedHashMap<>();
    private int correct;
    private int incorrect;

    public void subscribe(ProgressObserver observer) {
        Objects.requireNonNull(observer, "observer");
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public boolean unsubscribe(ProgressObserver observer) {
        return observers.remove(observer);
    }

    public void recordAnswer(String exerciseId, AnswerResult result) {
        if (exerciseId == null || exerciseId.isBlank()) {
            throw new IllegalArgumentException("exerciseId must not be blank");
        }
        Objects.requireNonNull(result, "result");

        if (result.correct()) {
            correct++;
        } else {
            incorrect++;
            mistakesByExerciseId.merge(exerciseId, 1, Integer::sum);
        }
        notifyObservers();
    }

    public ProgressSnapshot snapshot() {
        return new ProgressSnapshot(correct, incorrect, mistakesByExerciseId);
    }

    private void notifyObservers() {
        ProgressSnapshot snapshot = snapshot();
        for (ProgressObserver observer : List.copyOf(observers)) {
            observer.onProgressUpdated(snapshot);
        }
    }
}
