package com.fluento.progress;

import java.io.PrintStream;
import java.util.Locale;
import java.util.Objects;

public final class ConsoleProgressObserver implements ProgressObserver {
    private final PrintStream out;

    public ConsoleProgressObserver() {
        this(System.out);
    }

    public ConsoleProgressObserver(PrintStream out) {
        this.out = Objects.requireNonNull(out, "out");
    }

    @Override
    public void onProgressUpdated(ProgressSnapshot snapshot) {
        out.println(String.format(Locale.ROOT,
                "Progress: correct=%d, incorrect=%d, accuracy=%.0f%%",
                snapshot.correct(), snapshot.incorrect(), snapshot.accuracyPercent()));
    }
}
