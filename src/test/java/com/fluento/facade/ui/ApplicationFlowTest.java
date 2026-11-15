package com.fluento.facade.ui;
import com.fluento.builder.catalog.SourceLanguage;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ApplicationFlowTest {
    private static <T extends Component> java.util.List<T> all(Container root,Class<T> type) {
        java.util.List<T> result=new ArrayList<>();
        for(Component c:root.getComponents()) {
            if(type.isInstance(c)) result.add(type.cast(c));
            if(c instanceof Container child) result.addAll(all(child,type));
        }
        return result;
    }
    private static JButton button(Container p,String name) {
        return all(p,JButton.class).stream().filter(b -> name.equals(b.getText())).findFirst().orElseThrow();
    }
    private static JComboBox<?> combo(Container p,String name) {
        return all(p,JComboBox.class).stream().filter(c -> name.equals(c.getName())).findFirst().orElseThrow();
    }
    private static void answer(FluentoPanel app,String text) {
        java.util.List<JToggleButton> choices=all(app,JToggleButton.class);
        if(!choices.isEmpty())choices.stream().filter(o->o.getText().equals(text)).findFirst().orElseThrow().doClick();
        else all(app,JTextField.class).getFirst().setText(text);
        button(app,"Check answer").doClick();
    }
    private static void layout(Container p) {
        p.doLayout();
        for(Component c:p.getComponents()) if(c instanceof Container child) layout(child);
    }
    private static void capture(FluentoPanel app,String name) {
        String directory=System.getProperty("fluento.captureDir");
        if(directory==null) return;
        try {
            app.setSize(1000,740); layout(app);
            BufferedImage image=new BufferedImage(1000,740,BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics=image.createGraphics(); app.printAll(graphics); graphics.dispose();
            Files.createDirectories(Path.of(directory));
            ImageIO.write(image,"png",Path.of(directory,name+".png").toFile());
        } catch(Exception error) { throw new IllegalStateException(error); }
    }
    @Test void completeRoundAndReviewWeakWordsThroughActualControls() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Theme.install(); FluentoPanel app=new FluentoPanel();
            capture(app,"01-dashboard");
            button(app,"Start Food").doClick(); button(app,"Start practice").doClick();
            button(app,"Show hint").doClick();
            assertTrue(all(app,JTextArea.class).getFirst().getText().contains("First letter: a"));
            capture(app,"02-practice-hint");
            answer(app,"apple"); button(app,"Next exercise").doClick();
            answer(app,"wrong");
            assertEquals(2,app.progress().total()); assertEquals(1,app.progress().incorrect());
            capture(app,"03-feedback");
            button(app,"Next exercise").doClick();
            String[] remaining={"water","milk","apple","bread","water","milk"};
            for(int i=0;i<remaining.length;i++) {
                answer(app,remaining[i]);
                button(app,i==remaining.length-1 ? "Finish round" : "Next exercise").doClick();
            }
            assertEquals(8,app.progress().total()); assertEquals(7,app.progress().correct());
            assertTrue(all(app,JLabel.class).stream().anyMatch(l -> l.getText().contains("7 / 8 correct")));
            capture(app,"04-complete");
            button(app,"All lessons").doClick();
            combo(app,"reviewStrategy").setSelectedIndex(1);
            button(app,"Start Food").doClick(); button(app,"Start practice").doClick();
            assertTrue(all(app,JLabel.class).stream().anyMatch(l -> l.getText().contains("хлеб")));
            capture(app,"05-weak-review");
            JTabbedPane tabs=all(app,JTabbedPane.class).getFirst();
            tabs.setSelectedIndex(0); button(app,"Show meaning").doClick();
            capture(app,"06-word-cards");
        });
    }
    @Test void returningToSessionPreservesAnswerAndCannotCountItTwice() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Theme.install(); FluentoPanel app=new FluentoPanel();
            button(app,"Start Food").doClick(); button(app,"Start practice").doClick(); answer(app,"apple");
            button(app,"All lessons").doClick(); button(app,"Continue Food").doClick();
            assertFalse(button(app,"Check answer").isEnabled());
            assertTrue(button(app,"Next exercise").isEnabled());
            assertEquals(1,app.progress().total());
            button(app,"Check answer").doClick(); assertEquals(1,app.progress().total());
        });
    }
    @Test void blankAnswersAndUnselectedChoicesDoNotAdvanceProgress() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Theme.install(); FluentoPanel app=new FluentoPanel();
            combo(app,"sourceLanguage").setSelectedItem(SourceLanguage.KAZAKH);
            button(app,"Start Food").doClick(); button(app,"Start practice").doClick();
            button(app,"Check answer").doClick();
            assertEquals(0,app.progress().total()); assertFalse(button(app,"Next exercise").isEnabled());
            for(String word:java.util.List.of("apple","bread","water","milk")) {
                answer(app,word); button(app,"Next exercise").doClick();
            }
            assertTrue(all(app,JToggleButton.class).stream().noneMatch(JToggleButton::isSelected));
            button(app,"Check answer").doClick(); assertEquals(4,app.progress().total());
        });
    }
    @Test void cardsComeFirstAndProgressCountsUniqueCorrectExercises() throws Exception {
        SwingUtilities.invokeAndWait(()->{
            Theme.install();FluentoPanel app=new FluentoPanel();button(app,"Start Food").doClick();
            assertEquals("Word cards",all(app,JTabbedPane.class).getFirst().getTitleAt(0));
            assertEquals(0,all(app,JTabbedPane.class).getFirst().getSelectedIndex());
            button(app,"Start practice").doClick();answer(app,"apple");
            button(app,"All lessons").doClick();
            JProgressBar bar=all(app,JProgressBar.class).stream().filter(b->"topicProgress:food".equals(b.getName())).findFirst().orElseThrow();
            assertEquals(1,bar.getValue());
            button(app,"Continue Food").doClick();button(app,"Restart round").doClick();
            assertEquals(1,app.progress().total());answer(app,"apple");button(app,"All lessons").doClick();
            assertEquals(1,all(app,JProgressBar.class).stream().filter(b->"topicProgress:food".equals(b.getName())).findFirst().orElseThrow().getValue());
            assertEquals(2,app.progress().total());
        });
    }
    @Test void choiceButtonsAndSkipWorkThroughTheUi() throws Exception {
        SwingUtilities.invokeAndWait(()->{
            Theme.install();FluentoPanel app=new FluentoPanel();button(app,"Start Food").doClick();button(app,"Start practice").doClick();
            for(int i=0;i<4;i++){button(app,"I don't know").doClick();button(app,"Next exercise").doClick();}
            assertEquals(4,all(app,JToggleButton.class).size());
            capture(app,"07-answer-buttons");
            answer(app,"apple");assertEquals(1,app.progress().correct());
            assertTrue(all(app,JToggleButton.class).stream().noneMatch(JToggleButton::isEnabled));
        });
    }
    @Test void topicSearchFiltersAndRecoversFromEmptyResults() throws Exception {
        SwingUtilities.invokeAndWait(()->{
            FluentoPanel app=new FluentoPanel();
            JTextField search=all(app,JTextField.class).stream().filter(f->"topicSearch".equals(f.getName())).findFirst().orElseThrow();
            search.setText("  ANIMALS  ");assertNotNull(button(app,"Start Animals"));
            assertEquals(1,all(app,JButton.class).stream().filter(b->b.getText().startsWith("Start ")).count());
            search.setText("mother");assertNotNull(button(app,"Start Family"));
            search.setText("nothing-matches");assertTrue(all(app,JLabel.class).stream().anyMatch(l->l.getText().startsWith("No topics found")));
            search.setText("");assertEquals(16,all(app,JButton.class).stream().filter(b->b.getText().startsWith("Start ")).count());
        });
    }
    @Test void correctAnswerAppearsOnceAndHintCannotReopenAfterAnswer() throws Exception {
        SwingUtilities.invokeAndWait(()->{
            FluentoPanel app=new FluentoPanel();button(app,"Start Food").doClick();button(app,"Start practice").doClick();
            answer(app,"apple");String feedback=all(app,JTextArea.class).getFirst().getText();
            assertEquals(1,feedback.split("Correct",-1).length-1);
            assertFalse(button(app,"Show hint").isVisible());assertFalse(button(app,"Show hint").isEnabled());
        });
    }
}
