package com.fluento.lessons.ui;

import com.fluento.lessons.catalog.DemoLessonCatalog;
import com.fluento.model.Lesson;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;

class LessonPanelsTest {
    private <T extends Component> List<T> components(Container root, Class<T> type) {
        List<T> result = new ArrayList<>();
        for (Component child : root.getComponents()) {
            if (type.isInstance(child)) result.add(type.cast(child));
            if (child instanceof Container container) result.addAll(components(container, type));
        }
        return result;
    }

    private JButton button(Container panel, String text) {
        return components(panel, JButton.class).stream()
                .filter(b -> b.getText().equals(text)).findFirst().orElseThrow();
    }

    @Test
    void lessonSelectionPassesActualLessonToCaller() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            var catalog = new DemoLessonCatalog();
            var selected = new AtomicReference<Lesson>();
            var panel = new LessonsPanel(catalog, selected::set);
            var buttons = components(panel, JButton.class);
            assertEquals(16, buttons.size());
            buttons.get(1).doClick();
            assertSame(catalog.findById("food").orElseThrow(), selected.get());
        });
    }

    @Test
    void flashcardsRevealResetAndRespectBoundaries() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            var panel = new FlashcardsPanel(new DemoLessonCatalog().wordsForLesson("food"));
            assertFalse(button(panel, "Previous").isEnabled());
            button(panel, "Show meaning").doClick();
            assertTrue(components(panel, JLabel.class).stream().anyMatch(l -> l.getText().equals("a round fruit that grows on trees")));
            button(panel, "Next").doClick();
            assertTrue(button(panel, "Show meaning").isEnabled());
            assertFalse(components(panel, JLabel.class).stream().anyMatch(l -> l.getText().equals("food made from flour and baked")));
            button(panel, "Next").doClick();
            button(panel, "Next").doClick();
            assertFalse(button(panel, "Next").isEnabled());
            button(panel, "Previous").doClick();
            assertTrue(button(panel, "Next").isEnabled());
        });
    }

    @Test
    void emptyFlashcardsDisableControls() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            var panel = new FlashcardsPanel(List.of());
            assertTrue(components(panel, JButton.class).stream().noneMatch(JButton::isEnabled));
        });
    }
}
