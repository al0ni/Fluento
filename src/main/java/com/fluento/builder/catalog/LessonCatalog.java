package com.fluento.builder.catalog;

import com.fluento.factory.model.Lesson;
import com.fluento.factory.model.Word;
import java.util.List;
import java.util.Optional;

public interface LessonCatalog {
    List<Lesson> findAll();
    Optional<Lesson> findById(String lessonId);

    List<Word> wordsForLesson(String lessonId);
}
