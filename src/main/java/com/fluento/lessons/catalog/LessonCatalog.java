package com.fluento.lessons.catalog;

import com.fluento.model.Lesson;
import com.fluento.model.Word;
import java.util.List;
import java.util.Optional;

public interface LessonCatalog {
    List<Lesson> findAll();
    Optional<Lesson> findById(String lessonId);

    List<Word> wordsForLesson(String lessonId);
}
