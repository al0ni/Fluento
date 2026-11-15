package com.fluento.progress;

import com.fluento.model.AnswerResult;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProgressObserverTest {
    private static final AnswerResult RIGHT = new AnswerResult(true, "Correct!");
    private static final AnswerResult WRONG = new AnswerResult(false, "Wrong");

    @Test
    void observerIsNotifiedAfterEveryAnswer() {
        ProgressTracker tracker = new ProgressTracker();
        List<ProgressSnapshot> received = new ArrayList<>();
        tracker.subscribe(received::add);

        tracker.recordAnswer("e1", RIGHT);
        tracker.recordAnswer("e2", WRONG);

        assertEquals(2, received.size());
        assertEquals(1, received.get(0).correct());
        assertEquals(1, received.get(1).incorrect());
    }

    @Test
    void unsubscribedObserverStopsReceivingNotifications() {
        ProgressTracker tracker = new ProgressTracker();
        StatisticsObserver observer = new StatisticsObserver();
        tracker.subscribe(observer);
        tracker.recordAnswer("e1", RIGHT);

        assertTrue(tracker.unsubscribe(observer));
        tracker.recordAnswer("e2", RIGHT);

        assertEquals(1, observer.updatesReceived());
        assertEquals(1, observer.latest().correct());
        assertFalse(tracker.unsubscribe(observer));
    }

    @Test
    void allSubscribersAreNotified() {
        ProgressTracker tracker = new ProgressTracker();
        StatisticsObserver first = new StatisticsObserver();
        StatisticsObserver second = new StatisticsObserver();
        tracker.subscribe(first);
        tracker.subscribe(second);

        tracker.recordAnswer("e1", WRONG);

        assertEquals(1, first.updatesReceived());
        assertEquals(1, second.updatesReceived());
    }

    @Test
    void sameObserverSubscribedTwiceIsNotifiedOnce() {
        ProgressTracker tracker = new ProgressTracker();
        StatisticsObserver observer = new StatisticsObserver();
        tracker.subscribe(observer);
        tracker.subscribe(observer);

        tracker.recordAnswer("e1", RIGHT);

        assertEquals(1, observer.updatesReceived());
    }

    @Test
    void observerMayUnsubscribeItselfDuringNotification() {
        ProgressTracker tracker = new ProgressTracker();
        StatisticsObserver other = new StatisticsObserver();
        ProgressObserver oneShot = new ProgressObserver() {
            @Override
            public void onProgressUpdated(ProgressSnapshot snapshot) {
                tracker.unsubscribe(this);
            }
        };
        tracker.subscribe(oneShot);
        tracker.subscribe(other);

        tracker.recordAnswer("e1", RIGHT);
        tracker.recordAnswer("e2", RIGHT);

        assertEquals(2, other.updatesReceived());
    }

    @Test
    void statisticsObserverStartsEmpty() {
        StatisticsObserver observer = new StatisticsObserver();

        assertEquals(0, observer.updatesReceived());
        assertEquals(0, observer.latest().total());
    }

    @Test
    void statisticsObserverKeepsLatestSnapshot() {
        ProgressTracker tracker = new ProgressTracker();
        StatisticsObserver observer = new StatisticsObserver();
        tracker.subscribe(observer);

        tracker.recordAnswer("water", WRONG);
        tracker.recordAnswer("apple", RIGHT);

        assertEquals(2, observer.updatesReceived());
        assertEquals(1, observer.latest().correct());
        assertEquals(1, observer.latest().incorrect());
        assertEquals(1, observer.latest().mistakesFor("water"));
        assertEquals(50.0, observer.latest().accuracyPercent(), 0.0001);
    }

    @Test
    void consoleObserverPrintsProgress() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ProgressTracker tracker = new ProgressTracker();
        tracker.subscribe(new ConsoleProgressObserver(new PrintStream(buffer, true, StandardCharsets.UTF_8)));

        tracker.recordAnswer("e1", RIGHT);
        tracker.recordAnswer("e2", RIGHT);
        tracker.recordAnswer("e3", RIGHT);
        tracker.recordAnswer("e4", RIGHT);
        tracker.recordAnswer("e5", WRONG);

        String output = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("correct=4, incorrect=1, accuracy=80%"), output);
        assertEquals(5, output.lines().count());
    }

    @Test
    void rejectsNullObserver() {
        assertThrows(NullPointerException.class, () -> new ProgressTracker().subscribe(null));
    }
}
