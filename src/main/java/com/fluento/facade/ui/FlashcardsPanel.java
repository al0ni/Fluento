package com.fluento.facade.ui;
import com.fluento.facade.ui.*;
import com.fluento.builder.catalog.Pronunciations;
import com.fluento.factory.model.Word;
import javax.swing.*;
import java.awt.*;
import java.util.List;
public final class FlashcardsPanel extends JPanel {
 private final List<Word> words;private int index;private boolean revealed;
 private final JLabel counter=Theme.label("",14,true),word=Theme.label("",42,true),ipa=Theme.label("",23,false),meaning=Theme.label("",25,false);
 private final JButton previous=Theme.button("Previous",false),reveal=Theme.button("Show meaning",true),next=Theme.button("Next",false);
 public FlashcardsPanel(List<Word> words){this(words,()->{});}
 public FlashcardsPanel(List<Word> words,Runnable practice){
  super(new BorderLayout(0,16));this.words=List.copyOf(words);setBorder(BorderFactory.createEmptyBorder(24,12,12,12));
  add(counter,BorderLayout.NORTH);JPanel card=Theme.card(new GridBagLayout());GridBagConstraints c=new GridBagConstraints();c.gridx=0;c.insets=new Insets(10,5,10,5);
  word.setHorizontalAlignment(SwingConstants.CENTER);ipa.setForeground(Theme.MUTED);ipa.setFont(new Font("Dialog",Font.PLAIN,23));ipa.setName("pronunciation");
  c.gridy=0;card.add(word,c);c.gridy=1;card.add(ipa,c);c.gridy=2;card.add(meaning,c);add(card,BorderLayout.CENTER);
  JPanel bottom=new JPanel(new BorderLayout(0,12));JPanel controls=new JPanel(new FlowLayout(FlowLayout.CENTER,10,0));
  controls.add(previous);controls.add(reveal);controls.add(next);JButton start=Theme.button("Start practice",true);controls.add(start);bottom.add(controls,BorderLayout.NORTH);
  JLabel help=Theme.label("← / →: previous / next    Space: show / hide meaning    Alt+P: practice",12,false);help.setHorizontalAlignment(SwingConstants.CENTER);bottom.add(help,BorderLayout.SOUTH);add(bottom,BorderLayout.SOUTH);
  previous.addActionListener(e->move(-1));next.addActionListener(e->move(1));reveal.addActionListener(e->flip());start.setEnabled(!words.isEmpty());start.addActionListener(e->practice.run());
  Shortcuts.bind(this,"LEFT","previous",()->move(-1));Shortcuts.bind(this,"RIGHT","next",()->move(1));Shortcuts.bind(this,"SPACE","flip",this::flip);
  Shortcuts.bind(this,"alt P","practice",practice);refresh();
 }
 private void flip(){if(words.isEmpty())return;revealed=!revealed;refresh();}
 private void move(int delta){int target=index+delta;if(target>=0&&target<words.size()){index=target;revealed=false;refresh();}}
 private void refresh(){
  boolean empty=words.isEmpty();counter.setText(empty?"No flashcards":"WORD "+(index+1)+" OF "+words.size());
  word.setText(empty?"No words in this lesson":words.get(index).english());
  ipa.setText(empty?"":Pronunciations.forWord(words.get(index).id()));
  meaning.setText(!empty&&revealed?words.get(index).translation():" ");
  reveal.setText(revealed?"Hide meaning":"Show meaning");reveal.setEnabled(!empty);
  previous.setEnabled(!empty&&index>0);next.setEnabled(!empty&&index<words.size()-1);
 }
}
