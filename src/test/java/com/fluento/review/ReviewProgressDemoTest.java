package com.fluento.review;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReviewProgressDemoTest {
    @Test
    void weakWordsAreShownFirstInSecondRound() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ReviewProgressDemo.run(new PrintStream(buffer, true, StandardCharsets.UTF_8));

        List<String> lines = buffer.toString(StandardCharsets.UTF_8).lines().toList();
        int round1 = lines.indexOf("Round 1 - Sequential Review");
        int round2 = lines.indexOf("Round 2 - Weak Words First");
        assertTrue(round1 >= 0 && round2 > round1, lines.toString());

        String firstOfRound1 = lines.stream().skip(round1 + 1).filter(l -> l.startsWith("  1.")).findFirst().orElseThrow();
        assertFalse(firstOfRound1.contains("water"), firstOfRound1);

        List<String> round2Exercises = lines.stream().skip(round2 + 1).filter(l -> l.startsWith("  ")).toList();
        assertTrue(round2Exercises.get(0).contains("water"), round2Exercises.toString());
        assertTrue(round2Exercises.get(1).contains("water"), round2Exercises.toString());
    }

    @Test
    void progressIsPrintedAfterEveryAnswer() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ReviewProgressDemo.run(new PrintStream(buffer, true, StandardCharsets.UTF_8));

        long progressLines = buffer.toString(StandardCharsets.UTF_8).lines()
                .filter(l -> l.startsWith("Progress:")).count();
        assertEquals(16, progressLines);
    }
}
