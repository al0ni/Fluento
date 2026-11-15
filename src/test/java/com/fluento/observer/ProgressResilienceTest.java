package com.fluento.observer;
import com.fluento.factory.model.AnswerResult;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class ProgressResilienceTest {
    private final AnswerResult right = new AnswerResult(true,"ok");
    @Test void failingObserverDoesNotBlockOthersOrLoseCommittedAnswer() {
        List<RuntimeException> errors = new ArrayList<>();
        ProgressTracker tracker = new ProgressTracker(errors::add);
        StatisticsObserver stats = new StatisticsObserver();
        tracker.subscribe(s -> { throw new IllegalStateException("failed"); });
        tracker.subscribe(stats);
        assertDoesNotThrow(() -> tracker.recordAnswer("one",right));
        assertEquals(1,tracker.snapshot().total()); assertEquals(1,stats.latest().total());
        assertEquals(1,errors.size());
    }
    @Test void nestedAnswersAreDeliveredChronologically() {
        ProgressTracker tracker = new ProgressTracker();
        List<Integer> observed = new ArrayList<>();
        tracker.subscribe(s -> { if(s.total()==1) tracker.recordAnswer("nested",right); });
        tracker.subscribe(s -> observed.add(s.total()));
        tracker.recordAnswer("first",right);
        assertEquals(List.of(1,2),observed); assertEquals(2,tracker.snapshot().total());
    }
    @Test void failingErrorReporterDoesNotBlockSubscribers() {
        ProgressTracker tracker = new ProgressTracker(e -> { throw new IllegalStateException("reporter"); });
        StatisticsObserver stats = new StatisticsObserver();
        tracker.subscribe(s -> { throw new IllegalStateException("observer"); }); tracker.subscribe(stats);
        assertDoesNotThrow(() -> tracker.recordAnswer("one",right)); assertEquals(1,stats.latest().total());
    }
    @Test void subscriptionDuringDeliveryStartsAtNextEvent() {
        ProgressTracker tracker = new ProgressTracker();
        StatisticsObserver later = new StatisticsObserver();
        tracker.subscribe(s -> tracker.subscribe(later));
        tracker.recordAnswer("one",right); assertEquals(0,later.updatesReceived());
        tracker.recordAnswer("two",right); assertEquals(1,later.updatesReceived());
    }
    @Test void snapshotsRejectInvalidCountersAndMistakes() {
        assertThrows(IllegalArgumentException.class,() -> new ProgressSnapshot(-1,0,Map.of()));
        assertThrows(IllegalArgumentException.class,() -> new ProgressSnapshot(0,-1,Map.of()));
        assertThrows(IllegalArgumentException.class,() -> new ProgressSnapshot(0,0,Map.of("a",-1)));
        assertThrows(IllegalArgumentException.class,() -> new ProgressSnapshot(0,0,Map.of(" ",1)));
    }
    @Test void snapshotDefensivelyCopiesSuppliedMap() {
        Map<String,Integer> map = new HashMap<>(); map.put("one",1);
        ProgressSnapshot snapshot = new ProgressSnapshot(0,1,map); map.put("one",20);
        assertEquals(1,snapshot.mistakesFor("one"));
    }
}
