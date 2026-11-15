package com.fluento.progress;

public interface ProgressObserver {
    void onProgressUpdated(ProgressSnapshot snapshot);
}
