package com.fluento.app;
import com.fluento.lessons.catalog.*;
import com.fluento.lessons.ui.FlashcardsPanel;
import com.fluento.model.Lesson;
import com.fluento.progress.*;
import com.fluento.review.*;
import com.fluento.session.*;
import java.awt.*;
import java.util.Locale;
import javax.swing.*;

public final class FluentoPanel extends JPanel implements ProgressObserver {
    private final ProgressTracker tracker = new ProgressTracker();
    private final StatisticsObserver statistics = new StatisticsObserver();
    private final JLabel totals = Theme.label("",15,true);
    private final JPanel content = new JPanel(new BorderLayout());
    private final JComboBox<SourceLanguage> language = new JComboBox<>(SourceLanguage.values());
    private final JComboBox<String> strategy = new JComboBox<>(new String[]{"Sequential Review","Weak Words First"});
    private final java.util.Map<String,java.util.Set<String>> mastered = new java.util.HashMap<>();
    private LessonFacade active;
    private JTabbedPane sessionTabs;
    private LessonSessionPanel practicePanel;
    private FlashcardsPanel cardsPanel;
    private DemoLessonCatalog activeCatalog;
    private Lesson activeLesson;
    public FluentoPanel() {
        super(new BorderLayout(0,22));
        setBorder(BorderFactory.createEmptyBorder(24,36,20,36));
        setPreferredSize(new Dimension(1000,740));
        tracker.subscribe(this); tracker.subscribe(statistics);
        JPanel brand = new JPanel(new BorderLayout());
        JPanel identity = new JPanel(new GridLayout(2,1,0,4));
        identity.add(Theme.label("fluento.",32,true));
        identity.add(Theme.label("Small steps. Fluent future.",14,false));
        brand.add(identity,BorderLayout.WEST); brand.add(totals,BorderLayout.EAST);
        add(brand,BorderLayout.NORTH); add(content,BorderLayout.CENTER);
        JLabel footer = Theme.label("Learn English at your pace  |  Progress is kept until you close the app",12,false);
        footer.setForeground(Theme.MUTED); add(footer,BorderLayout.SOUTH);
        language.setName("sourceLanguage"); strategy.setName("reviewStrategy");
        onProgressUpdated(tracker.snapshot()); showDashboard();
    }
    @Override public void onProgressUpdated(ProgressSnapshot s) {
        totals.setText(String.format(Locale.ROOT,"%d answers   /   %d correct   /   %.0f%% accuracy",
            s.total(),s.correct(),s.accuracyPercent()));
    }
    public ProgressSnapshot progress() { return statistics.latest(); }
    private void showDashboard() {
        JPanel page = new JPanel(new BorderLayout(0,22));
        JPanel intro = new JPanel(new GridLayout(0,1,0,8));
        intro.add(Theme.label("A little practice. A lot of progress.",28,true));
        intro.add(Theme.label("Choose a topic, explore the words, then put them into practice.",15,false));
        JPanel settings = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        settings.add(new JLabel("Learn from")); settings.add(language);
        settings.add(new JLabel("Review order")); settings.add(strategy); intro.add(settings);
        page.add(intro,BorderLayout.NORTH);
        DemoLessonCatalog catalog=new DemoLessonCatalog();
        JTextField search=new JTextField();search.setName("topicSearch");
        search.setToolTipText("Search topics or English words");
        JPanel searchRow=new JPanel(new BorderLayout(10,0));searchRow.add(new JLabel("Search topics"),BorderLayout.WEST);searchRow.add(search,BorderLayout.CENTER);intro.add(searchRow);
        JPanel cards=new JPanel(new GridLayout(0,2,18,18));
        JScrollPane scroll=new JScrollPane(cards);scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(24);scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        Runnable filter=()->{
            cards.removeAll();String query=search.getText().strip().toLowerCase(Locale.ROOT);
            for(Lesson lesson:catalog.findAll()){
                boolean matches=lesson.title().toLowerCase(Locale.ROOT).contains(query)
                    ||catalog.wordsForLesson(lesson.id()).stream().anyMatch(w->w.english().toLowerCase(Locale.ROOT).contains(query));
                if(!matches)continue;
                JPanel card=Theme.card(new BorderLayout(0,12));card.setPreferredSize(new Dimension(410,170));
                card.add(Theme.label(lesson.title(),22,true),BorderLayout.NORTH);
                int solved=mastered.getOrDefault(lesson.id(),java.util.Set.of()).size();
                JProgressBar topicProgress=new JProgressBar(0,lesson.exercises().size());topicProgress.setValue(solved);topicProgress.setStringPainted(true);
                topicProgress.setString(solved+" / "+lesson.exercises().size()+" exercises solved");topicProgress.setName("topicProgress:"+lesson.id());
                card.add(topicProgress,BorderLayout.CENTER);
                JButton start=Theme.button("Start "+lesson.title(),true);start.addActionListener(e->startLesson(lesson.id()));card.add(start,BorderLayout.SOUTH);cards.add(card);
            }
            if(cards.getComponentCount()==0)cards.add(Theme.label("No topics found. Try another word.",16,false));
            cards.revalidate();cards.repaint();scroll.getVerticalScrollBar().setValue(0);
        };
        search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
            public void insertUpdate(javax.swing.event.DocumentEvent e){filter.run();}
            public void removeUpdate(javax.swing.event.DocumentEvent e){filter.run();}
            public void changedUpdate(javax.swing.event.DocumentEvent e){filter.run();}
        });filter.run();page.add(scroll,BorderLayout.CENTER);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        if(active != null && !active.isComplete()) {
            JButton resume = Theme.button("Continue " + active.lessonTitle(),false);
            resume.addActionListener(e -> showSession()); bottom.add(resume);
        }
        bottom.add(Theme.label("16 topics  /  112 words  /  224 exercises  /  3 source languages",13,false));
        page.add(bottom,BorderLayout.SOUTH); setPage(page);
    }
    private void startLesson(String id) {
        activeCatalog = new DemoLessonCatalog((SourceLanguage)language.getSelectedItem());
        activeLesson = activeCatalog.findById(id).orElseThrow();
        ReviewStrategy selected = strategy.getSelectedIndex()==0
            ? new SequentialReviewStrategy() : new WeakWordsFirstStrategy();
        active = new LessonFacade(activeLesson,selected,tracker,(lessonId,exerciseId,result)->{
            if(result.correct())mastered.computeIfAbsent(lessonId,key->new java.util.HashSet<>()).add(exerciseId);
        });
        sessionTabs=null;showSession();
    }
    private void showSession() {
        JPanel page = new JPanel(new BorderLayout(0,14));
        JPanel navigation = new JPanel(new BorderLayout());
        JButton back = Theme.button("All lessons",false); back.addActionListener(e -> showDashboard());
        navigation.add(back,BorderLayout.WEST);
        navigation.add(Theme.label(active.lessonTitle() + "  /  English practice",23,true),BorderLayout.EAST);
        page.add(navigation,BorderLayout.NORTH);
        if(sessionTabs==null){
            sessionTabs=new JTabbedPane();
            cardsPanel=new FlashcardsPanel(activeCatalog.wordsForLesson(activeLesson.id()),()->sessionTabs.setSelectedIndex(1));
            practicePanel=new LessonSessionPanel(active,this::restartRound);
            sessionTabs.addTab("Word cards",cardsPanel);
            sessionTabs.addTab("Practice",practicePanel);
        }
        page.add(sessionTabs,BorderLayout.CENTER);
        JPanel tools=new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        JButton restart=Theme.button("Restart round",false);restart.addActionListener(e->restartRound());tools.add(restart);
        tools.add(Theme.label("New rounds keep your overall progress. Cards first, practice when ready.",12,false));
        page.add(tools,BorderLayout.SOUTH);
        setPage(page);
    }
    private void restartRound(){
        active.restart();
        practicePanel=new LessonSessionPanel(active,this::restartRound);
        sessionTabs.setComponentAt(1,practicePanel);sessionTabs.setSelectedIndex(1);
    }
    private void setPage(JComponent page) {
        content.removeAll(); content.add(page); content.revalidate(); content.repaint();
    }
}
