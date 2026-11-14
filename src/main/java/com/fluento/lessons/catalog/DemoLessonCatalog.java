package com.fluento.lessons.catalog;

import com.fluento.lessons.builder.LessonBuilder;
import com.fluento.lessons.factory.*;
import com.fluento.model.*;
import com.fluento.session.HintDecorator;
import java.util.*;

public final class DemoLessonCatalog implements LessonCatalog {
    private final Map<String, Lesson> lessons = new LinkedHashMap<>();
    private final Map<String, List<Word>> vocabulary = new LinkedHashMap<>();

    private final SourceLanguage sourceLanguage;
    private final Properties sourceWords;

    public DemoLessonCatalog() {
        this(SourceLanguage.ENGLISH);
    }

    public DemoLessonCatalog(SourceLanguage sourceLanguage) {
        this.sourceLanguage = Objects.requireNonNull(sourceLanguage, "sourceLanguage");
        this.sourceWords = sourceLanguage.loadVocabulary();
        add("basics", "Basics", List.of(
                word("hello"),
                word("yes"),
                word("no"),
                word("thanks")));
        add("food", "Food", List.of(
                word("apple"),
                word("bread"),
                word("water"),
                word("milk")));
        add("travel", "Travel", List.of(
                word("ticket"),
                word("airport"),
                word("hotel"),
                word("train")));
        add("daily-life", "Daily Life", List.of(
                word("morning"),
                word("work"),
                word("home"),
                word("book")));
    }

    public SourceLanguage sourceLanguage() {
        return sourceLanguage;
    }

    private Word word(String id) {
        String meaning = sourceWords.getProperty(id);
        if (meaning == null || meaning.isBlank()) {
            throw new IllegalStateException("Missing meaning for word: " + id);
        }
        return new Word(id, id, meaning);
    }

    private void add(String id, String title, List<Word> words) {
        ExerciseCreator translations = new TranslationExerciseCreator();
        ExerciseCreator choices = new MultipleChoiceExerciseCreator(words);
        Lesson lesson = new LessonBuilder().id(id).title(title)
                .addExercises(exercisesWithHints(translations, words))
                .addExercises(exercisesWithHints(choices, words))
                .build();
        lessons.put(id, lesson);
        vocabulary.put(id, List.copyOf(words));
    }

    private List<Exercise> exercisesWithHints(ExerciseCreator creator, List<Word> words) {
        List<Exercise> exercises = new ArrayList<>();
        for (Word word : words) {
            String english = word.english().strip();
            String hint = "First letter: " + english.substring(0, 1)
                    + ". Length: " + english.length() + " letters.";
            exercises.add(new HintDecorator(creator.createExercise(word), hint));
        }
        return exercises;
    }

    @Override
    public List<Lesson> findAll() {
        return List.copyOf(lessons.values());
    }

    @Override
    public Optional<Lesson> findById(String lessonId) {
        return Optional.ofNullable(lessons.get(lessonId));
    }

    @Override
    public List<Word> wordsForLesson(String lessonId) {
        List<Word> words = vocabulary.get(lessonId);
        if (words == null) {
            throw new IllegalArgumentException("Unknown lesson: " + lessonId);
        }
        return words;
    }
}
