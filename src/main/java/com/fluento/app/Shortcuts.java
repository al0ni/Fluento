package com.fluento.app;
import javax.swing.*;
import java.awt.event.ActionEvent;

public final class Shortcuts {
 private Shortcuts() {}
 public static void bind(JComponent scope,String stroke,String id,Runnable action) {
  scope.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(stroke),id);
  scope.getActionMap().put(id,new AbstractAction(){public void actionPerformed(ActionEvent e){action.run();}});
 }
}
