package com.fluento.session;
import com.fluento.lessons.catalog.*;
import com.fluento.model.*;
import com.fluento.progress.*;
import com.fluento.review.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class LessonFacadeTest {
    private Lesson food() {
        Lesson original = new DemoLessonCatalog(SourceLanguage.RUSSIAN).findById("food").orElseThrow();
        return new Lesson(original.id(), original.title(), original.exercises().stream()
                .map(exercise -> (Exercise) new FeedbackDecorator(exercise)).toList());
    }
    @Test void sixPatternsWorkTogetherAndWeakExerciseLeadsNextRound() {
        Lesson lesson = food(); ProgressTracker tracker = new ProgressTracker();
        StatisticsObserver observer = new StatisticsObserver(); tracker.subscribe(observer);
        LessonFacade facade = new LessonFacade(lesson,new SequentialReviewStrategy(),tracker);
        assertTrue(facade.currentExercise().hint().isPresent());
        int count=0;
        while(!facade.isComplete()) {
            String id = facade.currentExerciseId();
            String answer = id.equals("translation-water") ? "wrong" : id.substring(id.indexOf('-')+1);
            AnswerResult result = facade.submitAnswer(answer);
            assertEquals(!id.equals("translation-water"),result.correct());
            assertTrue(result.feedback().contains(result.correct() ? "Next exercise" : "Weak Words First"));
            facade.nextExercise(); count++;
        }
        assertEquals(8,count); assertEquals(7,facade.score());
        assertEquals(8,observer.updatesReceived()); assertEquals(87.5,facade.progress().accuracyPercent());
        LessonFacade repeat = new LessonFacade(lesson,new WeakWordsFirstStrategy(),tracker);
        assertEquals("translation-water",repeat.currentExerciseId());
        assertEquals("translation-apple",lesson.exercises().getFirst().id());
    }
    @Test void doubleSubmissionCannotChangeScoreOrProgress() {
        ProgressTracker tracker = new ProgressTracker();
        LessonFacade facade = new LessonFacade(food(),new SequentialReviewStrategy(),tracker);
        facade.submitAnswer("apple");
        assertThrows(IllegalStateException.class,() -> facade.submitAnswer("apple"));
        assertEquals(1,facade.score()); assertEquals(1,tracker.snapshot().total());
    }
    @Test void invalidInputAndSkippingDoNotChangeState() {
        LessonFacade facade = new LessonFacade(food());
        assertThrows(IllegalArgumentException.class,() -> facade.submitAnswer(" "));
        assertThrows(IllegalArgumentException.class,() -> facade.submitAnswer(null));
        assertThrows(IllegalStateException.class,facade::nextExercise);
        assertEquals(0,facade.answeredCount()); assertEquals(0,facade.progress().total());
        assertEquals("",facade.currentFeedback());
    }
    @Test void completedSessionRejectsFurtherActions() {
        LessonFacade facade = new LessonFacade(food());
        for(int i=0;i<8;i++) { facade.submitAnswer("wrong"); facade.nextExercise(); }
        assertTrue(facade.isComplete()); assertEquals(8,facade.answeredCount());
        assertThrows(IllegalStateException.class,facade::nextExercise);
        assertThrows(IllegalStateException.class,facade::currentExercise);
        assertThrows(IllegalStateException.class,() -> facade.submitAnswer("apple"));
        assertEquals(8,facade.progress().total());
    }
    @Test void optionalListenerReceivesActualIdsAndCannotUndoAnswer() {
        List<String> received = new ArrayList<>();
        LessonFacade facade = new LessonFacade(food(),(lesson,id,result) -> {
            received.add(lesson+":"+id); throw new IllegalStateException("listener failed");
        });
        assertDoesNotThrow(() -> facade.submitAnswer("apple"));
        assertEquals(List.of("food:translation-apple"),received);
        assertTrue(facade.hasAnsweredCurrentExercise()); assertEquals(1,facade.progress().total());
    }
    @Test void allSourceLanguagesAndTopicsCanBeCompleted() {
        for(SourceLanguage language : SourceLanguage.values()) {
            ProgressTracker tracker = new ProgressTracker();
            for(Lesson lesson : new DemoLessonCatalog(language).findAll()) {
                LessonFacade facade = new LessonFacade(lesson,new SequentialReviewStrategy(),tracker);
                while(!facade.isComplete()) {
                    String id=facade.currentExerciseId();
                    assertTrue(facade.submitAnswer(id.substring(id.indexOf('-')+1)).correct());
                    facade.nextExercise();
                }
                assertEquals(lesson.exercises().size(),facade.score());
            }
            assertEquals(new DemoLessonCatalog(language).findAll().stream().mapToInt(l -> l.exercises().size()).sum(),tracker.snapshot().correct());
        }
    }
    @Test void rejectsMissingDependencies() {
        assertThrows(NullPointerException.class,() -> new LessonFacade(null));
        assertThrows(NullPointerException.class,() -> new LessonFacade(food(),null,new ProgressTracker()));
        assertThrows(NullPointerException.class,() -> new LessonFacade(food(),new SequentialReviewStrategy(),null));
    }
}
