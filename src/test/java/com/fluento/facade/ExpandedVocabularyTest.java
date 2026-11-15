package com.fluento.facade;
import com.fluento.builder.catalog.*;
import com.fluento.observer.*;
import com.fluento.strategy.*;
import com.fluento.facade.*;
import com.fluento.decorator.*;
import org.junit.jupiter.api.*;
import java.util.*;
import java.util.stream.*;
import static org.junit.jupiter.api.Assertions.*;
class ExpandedVocabularyTest {
 @TestFactory Stream<DynamicTest> everyExerciseInEveryLanguage(){
  var cases=new ArrayList<DynamicTest>();
  for(var language:SourceLanguage.values()){
   var catalog=new DemoLessonCatalog(language);
   for(var lesson:catalog.findAll())for(var exercise:lesson.exercises()){
    cases.add(DynamicTest.dynamicTest(language+" / "+exercise.id(),()->{
     String word=exercise.id().split("-",2)[1];
     assertTrue(exercise.checkAnswer(word).correct());assertFalse(exercise.checkAnswer("wrong-answer").correct());
     assertFalse(Pronunciations.forWord(word).isBlank());assertFalse(exercise.view().prompt().isBlank());
     assertTrue(exercise.view().hint().isPresent());
     if(!exercise.view().options().isEmpty()){
      assertEquals(4,new HashSet<>(exercise.view().options()).size());assertTrue(exercise.view().options().contains(word));
     }
    }));
   }
  }
  assertEquals(672,cases.size());return cases.stream();
 }
 @Test void allNewTopicsSupportSixteenQuestionRoundsAndWeakReview(){
  for(var lesson:new DemoLessonCatalog().findAll().subList(4,16)){
   var tracker=new ProgressTracker();var session=new LessonFacade(lesson,new SequentialReviewStrategy(),tracker);
   String first=session.currentExerciseId();session.skipAnswer();session.nextExercise();
   while(!session.isComplete()){assertTrue(session.submitAnswer(session.currentExerciseId().split("-",2)[1]).correct());session.nextExercise();}
   assertEquals(16,session.answeredCount());assertEquals(15,session.score());
   var repeat=new LessonFacade(lesson,new WeakWordsFirstStrategy(),tracker);assertEquals(first,repeat.currentExerciseId());
  }
 }
}
