package com.fluento;
import com.fluento.lessons.catalog.*;
import com.fluento.lessons.factory.*;
import com.fluento.model.*;
import com.fluento.progress.*;
import com.fluento.review.*;
import com.fluento.session.*;
import org.junit.jupiter.api.*;
import java.util.*;
import java.util.stream.*;
import static org.junit.jupiter.api.Assertions.*;

class AcceptanceMatrixTest {
 @TestFactory Stream<DynamicTest> acceptanceMatrix() {
  List<DynamicTest> tests=new ArrayList<>();
  for(SourceLanguage language:SourceLanguage.values()){
   var catalog=new DemoLessonCatalog(language);
   for(Lesson lesson:catalog.findAll().subList(0,4)) {
    for(Exercise exercise:lesson.exercises()) {
     tests.add(DynamicTest.dynamicTest(language+" / "+exercise.id()+" / answer and immutable view",()->{
      String answer=exercise.id().substring(exercise.id().indexOf('-')+1);
      assertTrue(exercise.checkAnswer("  "+answer.toUpperCase(Locale.ROOT)+"  ").correct());
      assertFalse(exercise.checkAnswer("not-an-answer").correct());
      assertFalse(exercise.checkAnswer(null).correct());
      assertFalse(exercise.checkAnswer(" ").correct());
      assertTrue(exercise.view().hint().orElseThrow().contains("\n\nLength:"));
      assertTrue(exercise.checkAnswer("wrong").feedback().contains("\n\n"));
      assertThrows(UnsupportedOperationException.class,()->exercise.view().options().add("x"));
     }));
    }
    for(boolean weak:List.of(false,true)){
     tests.add(DynamicTest.dynamicTest(language+" / "+lesson.id()+" / "+(weak?"weak":"sequential")+" complete mixed round",()->{
      ProgressTracker tracker=new ProgressTracker();
      StatisticsObserver observer=new StatisticsObserver();tracker.subscribe(observer);
      ReviewStrategy strategy=weak?new WeakWordsFirstStrategy():new SequentialReviewStrategy();
      String difficult=lesson.exercises().getLast().id();
      tracker.recordAnswer(difficult,new AnswerResult(false,"wrong"));
      LessonFacade facade=new LessonFacade(lesson,strategy,tracker);
      if(weak)assertEquals(difficult,facade.currentExerciseId());
      Set<String> visited=new HashSet<>();
      while(!facade.isComplete()){
       String id=facade.currentExerciseId();assertTrue(visited.add(id));
       String answer=id.substring(id.indexOf('-')+1);
       if(visited.size()%2==0)assertFalse(facade.skipAnswer().correct());
       else assertTrue(facade.submitAnswer(answer).correct());
       assertThrows(IllegalStateException.class,()->facade.submitAnswer(answer));
       assertThrows(IllegalStateException.class,facade::skipAnswer);
       facade.nextExercise();
      }
      assertEquals(8,visited.size());assertEquals(4,facade.score());
      assertEquals(9,tracker.snapshot().total());assertEquals(9,observer.updatesReceived());
      assertEquals(5,tracker.snapshot().incorrect());
      assertThrows(IllegalStateException.class,facade::nextExercise);
      facade.restart();assertEquals(0,facade.score());assertEquals(0,facade.answeredCount());
      assertEquals(9,tracker.snapshot().total());
     }));
    }
   }
  }
  for(int position=0;position<8;position++)for(boolean skipped:List.of(false,true)){
   int target=position;
   tests.add(DynamicTest.dynamicTest("restart at exercise "+target+" / "+(skipped?"skip":"correct"),()->{
    var lesson=new DemoLessonCatalog().findById("food").orElseThrow();
    var facade=new LessonFacade(lesson);
    for(int i=0;i<target;i++){facade.skipAnswer();facade.nextExercise();}
    if(skipped)facade.skipAnswer();else facade.submitAnswer(facade.currentExerciseId().split("-",2)[1]);
    int before=facade.progress().total();facade.restart();
    assertEquals("translation-apple",facade.currentExerciseId());assertFalse(facade.hasAnsweredCurrentExercise());
    assertEquals(before,facade.progress().total());assertEquals("",facade.currentFeedback());
    assertThrows(IllegalStateException.class,facade::nextExercise);
   }));
  }
  List<Runnable> boundaries=List.of(
   ()->assertThrows(IllegalArgumentException.class,()->new TranslationExercise(new Word(" ","a","a"))),
   ()->assertThrows(IllegalArgumentException.class,()->new TranslationExercise(new Word("a"," ","a"))),
   ()->assertThrows(IllegalArgumentException.class,()->new TranslationExercise(new Word("a","a"," "))),
   ()->assertThrows(NullPointerException.class,()->new TranslationExercise(null)),
   ()->assertThrows(IllegalArgumentException.class,()->new MultipleChoiceExercise(new Word("a","a","a"),List.of("a"))),
   ()->assertThrows(IllegalArgumentException.class,()->new MultipleChoiceExercise(new Word("a","a","a"),List.of("a","A"))),
   ()->assertThrows(IllegalArgumentException.class,()->new MultipleChoiceExercise(new Word("a","a","a"),List.of("b","c"))),
   ()->assertThrows(IllegalArgumentException.class,()->new MultipleChoiceExercise(new Word("a","a","a"),List.of("a"," "))),
   ()->assertThrows(NullPointerException.class,()->new MultipleChoiceExercise(new Word("a","a","a"),null)),
   ()->{var f=new LessonFacade(new DemoLessonCatalog().findById("food").orElseThrow());assertThrows(IllegalArgumentException.class,()->f.submitAnswer("\t\n"));assertEquals(0,f.progress().total());},
   ()->{var t=new ProgressTracker();var o=new StatisticsObserver();t.subscribe(o);t.unsubscribe(o);t.recordAnswer("a",new AnswerResult(false,""));assertEquals(0,o.updatesReceived());},
   ()->{var l=new DemoLessonCatalog().findById("food").orElseThrow();var order=new WeakWordsFirstStrategy().order(l.exercises(),Map.of(l.exercises().getLast().id(),Integer.MAX_VALUE));assertEquals(l.exercises().getLast(),order.getFirst());},
   ()->{Exercise special=new Exercise(){public String id(){return "special";}public ExerciseView view(){return new ExerciseView("q",List.of(),Optional.empty());}public AnswerResult checkAnswer(String answer){return new AnswerResult("[skipped]".equals(answer),"feedback");}};
       var f=new LessonFacade(new Lesson("special","Special",List.of(special)));assertFalse(f.skipAnswer().correct());assertEquals(0,f.score());assertEquals(1,f.progress().incorrect());},
   ()->{var t=new ProgressTracker();var events=new ArrayList<Integer>();t.subscribe(s->{if(s.total()<4)t.recordAnswer("nested",new AnswerResult(true,""));});t.subscribe(s->events.add(s.total()));t.recordAnswer("start",new AnswerResult(true,""));assertEquals(List.of(1,2,3,4),events);}
  );
  String[] names={"blank word id","blank English","blank meaning","null word","one choice","duplicate choices","missing correct choice","blank choice","null choices","blank input atomicity","unsubscribe","max mistake ordering","skip cannot match sentinel","nested FIFO notifications"};
  for(int i=0;i<boundaries.size();i++){Runnable check=boundaries.get(i);tests.add(DynamicTest.dynamicTest(names[i],check::run));}
  assertEquals(150,tests.size());
  return tests.stream();
 }
}
