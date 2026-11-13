package com.fluento.lessons.factory;

import com.fluento.model.Exercise;
import com.fluento.model.Word;
import java.util.*;

public final class MultipleChoiceExerciseCreator extends ExerciseCreator {
    private final List<String> answerPool;

    public MultipleChoiceExerciseCreator(List<Word> vocabulary) {
        Objects.requireNonNull(vocabulary, "vocabulary");
        Map<String, String> unique = new LinkedHashMap<>();
        for (Word word : vocabulary) {
            ExerciseSupport.requireWord(word);
            unique.putIfAbsent(ExerciseSupport.normalize(word.english()),
                    word.english().strip());
        }
        if (unique.size() < 2) {
            throw new IllegalArgumentException("Vocabulary needs two distinct words");
        }
        answerPool = List.copyOf(unique.values());
    }

    @Override
    public Exercise createExercise(Word word) {
        ExerciseSupport.requireWord(word);
        String answer = word.english().strip();
        List<String> options = new ArrayList<>();
        options.add(answer);
        answerPool.stream()
                .filter(value -> !ExerciseSupport.normalize(value)
                        .equals(ExerciseSupport.normalize(answer)))
                .limit(3)
                .forEach(options::add);
        Collections.rotate(options, Math.floorMod(word.id().hashCode(), options.size()));
        return new MultipleChoiceExercise(word, options);
    }
}
