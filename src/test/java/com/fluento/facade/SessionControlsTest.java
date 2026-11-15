package com.fluento.facade;
import com.fluento.builder.catalog.*;
import com.fluento.facade.ui.Theme;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import static org.junit.jupiter.api.Assertions.*;
class SessionControlsTest {
 private LessonFacade session(){return new LessonFacade(new DemoLessonCatalog().findById("food").orElseThrow());}
 private JButton button(Container root,String text){for(Component c:root.getComponents()){
  if(c instanceof JButton b&&b.getText().equals(text))return b;
  if(c instanceof Container child){JButton b=button(child,text);if(b!=null)return b;}
 }return null;}
 @Test void skipCountsOnceAndRestartPreservesCumulativeProgress(){
  LessonFacade s=session();assertFalse(s.skipAnswer().correct());
  assertEquals(1,s.progress().incorrect());assertEquals(1,s.progress().mistakesFor(s.currentExerciseId()));
  assertThrows(IllegalStateException.class,s::skipAnswer);s.nextExercise();s.submitAnswer("bread");
  s.restart();assertEquals("translation-apple",s.currentExerciseId());assertEquals(0,s.score());
  assertEquals(0,s.answeredCount());assertEquals(2,s.progress().total());
 }
 @Test void restartWorksAfterCompletedRound(){LessonFacade s=session();
  for(int i=0;i<8;i++){s.skipAnswer();s.nextExercise();}
  assertThrows(IllegalStateException.class,s::skipAnswer);s.restart();
  assertFalse(s.isComplete());assertFalse(s.hasAnsweredCurrentExercise());assertEquals(8,s.progress().total());
 }
 @Test void hintToggleAndKeyboardActionsRespectAnswerState()throws Exception{
  SwingUtilities.invokeAndWait(()->{Theme.install();LessonFacade s=session();LessonSessionPanel p=new LessonSessionPanel(s);
   button(p,"Show hint").doClick();assertNotNull(button(p,"Hide hint"));
   p.getActionMap().get("hint").actionPerformed(new ActionEvent(p,0,"hint"));assertNotNull(button(p,"Show hint"));
   p.getActionMap().get("skip").actionPerformed(new ActionEvent(p,0,"skip"));
   assertEquals(1,s.progress().total());p.getActionMap().get("skip").actionPerformed(new ActionEvent(p,0,"skip"));assertEquals(1,s.progress().total());
   p.getActionMap().get("primary").actionPerformed(new ActionEvent(p,0,"primary"));assertEquals("translation-bread",s.currentExerciseId());
  });
 }
 @Test void everyCatalogWordHasPronunciation(){for(var language:SourceLanguage.values()){
  var catalog=new DemoLessonCatalog(language);for(var lesson:catalog.findAll())for(var word:catalog.wordsForLesson(lesson.id()))
   assertFalse(Pronunciations.forWord(word.id()).isBlank(),word.id());
 }assertEquals("",Pronunciations.forWord("unknown"));}
 @Test void standalonePanelRestartsWithoutAccumulatingButtons() throws Exception {
  SwingUtilities.invokeAndWait(()->{
   LessonFacade s=session();LessonSessionPanel panel=new LessonSessionPanel(s);
   for(int round=0;round<2;round++){
    for(int i=0;i<8;i++){button(panel,"I don't know").doClick();button(panel,i==7?"Finish round":"Next exercise").doClick();}
    assertTrue(s.isComplete());button(panel,"Restart round").doClick();
    assertFalse(s.isComplete());assertEquals(0,s.answeredCount());assertEquals((round+1)*8,s.progress().total());
   }
  });
 }
}
