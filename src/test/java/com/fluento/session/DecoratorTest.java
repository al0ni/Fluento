package com.fluento.session;
import com.fluento.lessons.factory.TranslationExercise;
import com.fluento.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class DecoratorTest {
    private Exercise base() { return new TranslationExercise(new Word("apple","apple","fruit")); }
    @Test void stackedDecoratorsPreserveIdentityViewAndCorrectness() {
        Exercise original=base();
        Exercise decorated=new FeedbackDecorator(new HintDecorator(original,"Starts with a"));
        assertEquals(original.id(),decorated.id());
        assertEquals(original.view().prompt(),decorated.view().prompt());
        assertEquals(original.view().options(),decorated.view().options());
        assertEquals("Starts with a",decorated.view().hint().orElseThrow());
        assertTrue(original.view().hint().isEmpty());
        assertTrue(decorated.checkAnswer("apple").correct());
        assertFalse(decorated.checkAnswer("pear").correct());
        assertTrue(decorated.checkAnswer("apple").feedback().contains("Next exercise"));
        assertTrue(decorated.checkAnswer("pear").feedback().contains("Weak Words First"));
    }
    @Test void decoratorOrderDoesNotChangeTheAnswer() {
        Exercise decorated=new HintDecorator(new FeedbackDecorator(base()),"  a  ");
        assertTrue(decorated.checkAnswer("apple").correct());
        assertEquals("a",decorated.view().hint().orElseThrow());
    }
    @Test void rejectsMissingExerciseOrBlankHint() {
        assertThrows(NullPointerException.class,() -> new FeedbackDecorator(null));
        assertThrows(NullPointerException.class,() -> new HintDecorator(null,"a"));
        assertThrows(IllegalArgumentException.class,() -> new HintDecorator(base()," "));
        assertThrows(IllegalArgumentException.class,() -> new HintDecorator(base(),null));
    }
}
