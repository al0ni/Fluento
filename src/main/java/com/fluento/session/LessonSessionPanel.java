package com.fluento.session;

import com.fluento.model.AnswerResult;
import com.fluento.model.ExerciseView;
import java.awt.GridLayout;
import javax.swing.*;

public final class LessonSessionPanel extends JPanel {
    private final LessonFacade session;
    private final JLabel progress = new JLabel();
    private final JLabel prompt = new JLabel();
    private final JLabel feedback = new JLabel(" ");
    private final JTextField answer = new JTextField();
    private final JComboBox<String> options = new JComboBox<>();
    private final JButton hint = new JButton("Show hint");
    private final JButton submit = new JButton("Submit");
    private final JButton next = new JButton("Next");

    public LessonSessionPanel(LessonFacade session) {
        super(new GridLayout(0, 1, 6, 6));
        this.session = session;
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(progress);
        add(prompt);
        add(answer);
        add(options);
        add(hint);
        add(feedback);
        add(submit);
        add(next);

        hint.addActionListener(event -> feedback.setText(session.currentExercise().hint().orElse(" ")));
        submit.addActionListener(event -> submitAnswer());
        next.addActionListener(event -> {
            session.nextExercise();
            showExercise();
        });
        showExercise();
    }

    private void submitAnswer() {
        String value = options.isVisible() ? (String) options.getSelectedItem() : answer.getText();
        if (value == null || value.isBlank()) {
            feedback.setText("Enter or select an answer.");
            return;
        }

        AnswerResult result = session.submitAnswer(value);
        feedback.setText(result.feedback());
        answer.setEnabled(false);
        options.setEnabled(false);
        hint.setEnabled(false);
        submit.setEnabled(false);
        next.setEnabled(true);
    }

    private void showExercise() {
        boolean complete = session.isComplete();
        answer.setVisible(false);
        options.setVisible(false);
        hint.setVisible(false);
        submit.setEnabled(!complete);
        next.setEnabled(false);
        feedback.setText(" ");

        if (complete) {
            progress.setText("Lesson complete");
            prompt.setText("Score: " + session.score() + " / " + session.exerciseCount());
            return;
        }

        ExerciseView view = session.currentExercise();
        progress.setText("Exercise " + (session.answeredCount() + 1) + " / " + session.exerciseCount());
        prompt.setText(view.prompt());
        answer.setText("");
        answer.setEnabled(true);
        options.setEnabled(true);
        options.removeAllItems();
        for (String option : view.options()) {
            options.addItem(option);
        }
        options.setSelectedIndex(-1);
        answer.setVisible(view.options().isEmpty());
        options.setVisible(!view.options().isEmpty());
        hint.setVisible(view.hint().isPresent());
        hint.setEnabled(true);
        next.setText(session.answeredCount() + 1 == session.exerciseCount() ? "Finish" : "Next");
    }
}
