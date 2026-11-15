package com.fluento.progress;

import com.fluento.model.AnswerResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

public final class ProgressTracker {
    private final List<ProgressObserver> observers = new ArrayList<>();
    private final Map<String, Integer> mistakesByExerciseId = new LinkedHashMap<>();
    private int correct;
    private int incorrect;
    private final Deque<ProgressSnapshot> pending = new ArrayDeque<>();
    private final Consumer<RuntimeException> errorHandler;
    private boolean notifying;

    public ProgressTracker() {
        this(error -> System.err.println("Progress observer failed: " + error.getMessage()));
    }

    public ProgressTracker(Consumer<RuntimeException> errorHandler) {
        this.errorHandler = Objects.requireNonNull(errorHandler, "errorHandler");
    }

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

        pending.addLast(snapshot());
        if (notifying) return;
        notifying = true;
        try {
            while (!pending.isEmpty()) {
                ProgressSnapshot next = pending.removeFirst();
                for (ProgressObserver observer : List.copyOf(observers)) {
                    try {
                        observer.onProgressUpdated(next);
                    } catch (RuntimeException error) {
                        try {
                            errorHandler.accept(error);
                        } catch (RuntimeException reportingError) {
                            System.err.println("Progress error handler failed: " + reportingError.getMessage());
                        }
                    }
                }
            }
        } finally {
            notifying = false;
        }
    }
}
