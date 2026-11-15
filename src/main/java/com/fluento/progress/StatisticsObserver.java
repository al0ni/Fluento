package com.fluento.progress;

public final class StatisticsObserver implements ProgressObserver {
    private ProgressSnapshot latest = ProgressSnapshot.empty();
    private int updatesReceived;

    @Override
    public void onProgressUpdated(ProgressSnapshot snapshot) {
        this.latest = snapshot;
        updatesReceived++;
    }

    public ProgressSnapshot latest() {
        return latest;
    }

    public int updatesReceived() {
        return updatesReceived;
    }
}
