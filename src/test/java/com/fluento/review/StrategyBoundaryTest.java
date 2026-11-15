package com.fluento.review;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class StrategyBoundaryTest {
    @Test void bothStrategiesHandleEmptyLists() {
        for(ReviewStrategy strategy : List.of(new SequentialReviewStrategy(),new WeakWordsFirstStrategy()))
            assertEquals(List.of(),strategy.order(List.of(),null));
    }
    @Test void weakStrategyRejectsMalformedCountsAndNullExercises() {
        WeakWordsFirstStrategy strategy = new WeakWordsFirstStrategy();
        assertThrows(IllegalArgumentException.class,() -> strategy.order(List.of(),Map.of("a",-1)));
        Map<String,Integer> nullValue=new HashMap<>(); nullValue.put("a",null);
        assertThrows(NullPointerException.class,() -> strategy.order(List.of(),nullValue));
        assertThrows(NullPointerException.class,() -> strategy.order(null,Map.of()));
        assertThrows(NullPointerException.class,() -> strategy.order(Arrays.asList((com.fluento.model.Exercise)null),Map.of()));
    }
}
