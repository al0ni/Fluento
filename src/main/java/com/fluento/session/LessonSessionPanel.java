package com.fluento.session;
import com.fluento.app.*;
import com.fluento.model.ExerciseView;
import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;

public final class LessonSessionPanel extends JPanel {
 private final LessonFacade session;
 private final JLabel progress=Theme.label("",14,true), prompt=Theme.label("",23,true);
 private final JTextArea feedback=new JTextArea(4,35);
 private final JTextField answer=new JTextField();
 private final JPanel choices=new JPanel(new GridLayout(0,2,12,12));
 private final java.util.List<JToggleButton> optionButtons=new ArrayList<>();
 private final JButton hint=Theme.button("Show hint",false), submit=Theme.button("Check answer",true),
   next=Theme.button("Next exercise",true), skip=Theme.button("I don't know",false);
 private final JProgressBar bar=new JProgressBar();
 private boolean hintVisible;
 private String selected;
 private final Runnable restart;
 private final JButton again=Theme.button("Restart round",true);
 public LessonSessionPanel(LessonFacade session){this(session,null);}
 public LessonSessionPanel(LessonFacade session,Runnable restart) {
  super(new BorderLayout(0,16));this.session=java.util.Objects.requireNonNull(session);this.restart=restart==null?()->{session.restart();hintVisible=false;selected=null;render();}:restart;
  setBorder(BorderFactory.createEmptyBorder(22,12,12,12));
  JPanel header=new JPanel(new BorderLayout(0,12));header.add(progress,BorderLayout.NORTH);
  bar.setMaximum(session.exerciseCount());bar.setPreferredSize(new Dimension(500,7));header.add(bar,BorderLayout.SOUTH);add(header,BorderLayout.NORTH);
  JPanel card=Theme.card(new BorderLayout(0,10));card.setBorder(BorderFactory.createEmptyBorder(14,20,14,20));
  prompt.setBorder(BorderFactory.createEmptyBorder(4,0,8,0));card.add(prompt,BorderLayout.NORTH);
  JPanel center=new JPanel();center.setOpaque(false);center.setLayout(new BoxLayout(center,BoxLayout.Y_AXIS));
  answer.setName("answerInput");answer.setFont(answer.getFont().deriveFont(20f));answer.setMaximumSize(new Dimension(Integer.MAX_VALUE,48));
  choices.setOpaque(false);choices.setName("answerChoices");choices.setMaximumSize(new Dimension(Integer.MAX_VALUE,120));
  center.add(answer);center.add(choices);center.add(Box.createVerticalStrut(8));
  feedback.setName("answerFeedback");feedback.setEditable(false);feedback.setFocusable(false);
  feedback.setLineWrap(true);feedback.setWrapStyleWord(true);feedback.setOpaque(false);feedback.setForeground(Theme.MUTED);
  feedback.setMaximumSize(new Dimension(Integer.MAX_VALUE,90));feedback.setPreferredSize(new Dimension(400,90));choices.setPreferredSize(new Dimension(500,106));choices.setMinimumSize(new Dimension(200,90));center.add(feedback);card.add(center,BorderLayout.CENTER);
  JPanel bottom=new JPanel(new BorderLayout(0,16));bottom.setOpaque(false);
  JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));actions.setOpaque(false);
  actions.add(hint);actions.add(skip);actions.add(submit);actions.add(next);actions.add(again);again.addActionListener(e->this.restart.run());bottom.add(actions,BorderLayout.NORTH);
  bottom.add(Theme.label("Enter: check / next    Alt+H: hint    Alt+S: skip    Alt+1–4: choose",12,false),BorderLayout.SOUTH);
  card.add(bottom,BorderLayout.SOUTH);add(card,BorderLayout.CENTER);
  hint.addActionListener(e->toggleHint());submit.addActionListener(e->submit(false));skip.addActionListener(e->submit(true));
  next.addActionListener(e->advance());
  Shortcuts.bind(this,"ENTER","primary",()->{if(!session.isComplete()){if(session.hasAnsweredCurrentExercise())advance();else submit(false);}});
  answer.addActionListener(e->{if(session.hasAnsweredCurrentExercise())advance();else submit(false);});
  Shortcuts.bind(this,"alt H","hint",()->{if(hint.isVisible()&&hint.isEnabled())toggleHint();});
  Shortcuts.bind(this,"alt S","skip",()->{if(skip.isVisible()&&skip.isEnabled())submit(true);});
  for(int i=0;i<4;i++){final int index=i;Shortcuts.bind(this,"alt "+(i+1),"choose"+i,()->{
   if(index<optionButtons.size()&&optionButtons.get(index).isEnabled())optionButtons.get(index).doClick();
  });}
  render();
 }
 private void toggleHint(){
  if(session.isComplete()||session.hasAnsweredCurrentExercise())return;
  hintVisible=!hintVisible;hint.setText(hintVisible?"Hide hint":"Show hint");
  feedback.setText(hintVisible?session.currentExercise().hint().orElse("No hint available."):"Choose an answer, or skip to see the solution.");
 }
 private void submit(boolean skipped){
  if(session.isComplete()||session.hasAnsweredCurrentExercise())return;
  try{
   if(skipped)session.skipAnswer();else session.submitAnswer(choices.isVisible()?selected:answer.getText());
   render();
  }catch(IllegalArgumentException e){feedback.setText("Please type an answer or choose one of the options. You can also skip.");}
 }
 private void advance(){if(!session.isComplete()&&session.hasAnsweredCurrentExercise()){session.nextExercise();hintVisible=false;selected=null;render();}}
 private void render(){
  boolean complete=session.isComplete(),answered=session.hasAnsweredCurrentExercise();
  bar.setValue(session.answeredCount());again.setVisible(complete);hint.setVisible(!complete&&!answered);skip.setVisible(!complete&&!answered);
  submit.setVisible(!complete);submit.setEnabled(!answered&&!complete);next.setVisible(!complete);next.setEnabled(answered&&!complete);
  if(complete){
   progress.setText("ROUND COMPLETE");prompt.setText("Round score: "+session.score()+" / "+session.exerciseCount()+" correct");
   answer.setVisible(false);choices.setVisible(false);feedback.setText("Round complete. Restart to practise again.");

  }else{
   ExerciseView view=session.currentExercise();int number=answered?session.answeredCount():session.answeredCount()+1;
   progress.setText("EXERCISE "+number+" OF "+session.exerciseCount());prompt.setText(view.prompt());
   answer.setVisible(view.options().isEmpty());choices.setVisible(!view.options().isEmpty());answer.setEnabled(!answered);
   if(!answered){
    answer.setText("");choices.removeAll();optionButtons.clear();ButtonGroup group=new ButtonGroup();
    for(String option:view.options()){
     JToggleButton b=new JToggleButton(option);b.setName("choice:"+option);b.setFont(b.getFont().deriveFont(17f));
     b.setFocusPainted(true);b.setBackground(Color.WHITE);b.setBorder(BorderFactory.createCompoundBorder(
       BorderFactory.createLineBorder(new Color(215,215,232)),BorderFactory.createEmptyBorder(12,16,12,16)));
     b.addActionListener(e->{selected=option;for(JToggleButton o:optionButtons)o.setBackground(o.isSelected()?Theme.PALE:Color.WHITE);});
     group.add(b);choices.add(b);optionButtons.add(b);
    }
   }
   optionButtons.forEach(b->b.setEnabled(!answered));hint.setText("Show hint");
   hint.setVisible(!answered&&view.hint().isPresent());hint.setEnabled(!answered);
   feedback.setText(answered?session.currentFeedback():"Choose an answer, or skip to see the solution.");
   next.setText(number==session.exerciseCount()?"Finish round":"Next exercise");
  }
  revalidate();repaint();
 }
}
