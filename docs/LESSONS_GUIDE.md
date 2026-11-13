# Fluento: lessons and exercises

## Run

Use Java 21. Reload the Maven project in IntelliJ after changing pom.xml.
Run com.fluento.lessons.demo.LessonsDemo for lessons and flashcards.
Select Russian, Kazakh, or English definitions under "Learn English from".
Russian is the initial UI selection. Returning to lessons preserves the selected language.
Run com.fluento.lessons.demo.ExerciseDemo for a console exercise demonstration;
select 1, 2, or 3 when prompted. Answers must be English words, not option numbers.
Run mvn test for automated checks. The existing app.Main is unchanged.

## Scope

Four lessons: Basics, Food, Travel, Daily Life.
Each has four words, four typed-answer exercises, and four multiple-choice exercises.
Java source, tests, validation messages, and UI labels are English with no code comments.
Russian and Kazakh vocabulary is stored separately in UTF-8 resource files.
The catalogue is built in; there is no database or saved progress in this module.

## Factory Method

Product: Exercise.
Concrete products: TranslationExercise and MultipleChoiceExercise.
Creator: ExerciseCreator.
Concrete creators: TranslationExerciseCreator and MultipleChoiceExerciseCreator.
The abstract createExercise(Word) method is overridden by each creator.
The shared createExercises(List<Word>) workflow invokes that factory method.
DemoLessonCatalog uses both creators through the ExerciseCreator type.
To add a new exercise type, implement Exercise and extend ExerciseCreator.

## Builder

LessonBuilder provides id, title, addExercise, addExercises, and build.
DemoLessonCatalog defines how the example lessons are assembled.
Lesson rejects missing identifiers, missing titles, and empty exercise lists.
The builder also rejects duplicate exercise identifiers.
Each built lesson takes an immutable snapshot, so reusing a builder cannot
change an already built lesson.

## Shared contract and source languages

Word(id, english, translation) keeps its original signature.
The translation field now contains the selected source-language meaning:
Russian translation, Kazakh translation, or English definition.
TranslationExercise retains its class name for compatibility, but always asks
for the English word from the source-language meaning.
Both exercise types check word.english(), ignoring case and outer whitespace.
Null, blank, incorrect words, and option numbers are rejected.
Synonyms and spelling corrections are not supported.
ExerciseView.options is empty for typed answers and contains English words
for multiple-choice questions. The correct option is not marked.
Feedback reveals the correct English answer after submission.

Example integration:

```java
LessonCatalog catalog = new DemoLessonCatalog(SourceLanguage.KAZAKH);
Lesson lesson = catalog.findById("food").orElseThrow();
Exercise exercise = lesson.exercises().getFirst();
ExerciseView view = exercise.view();
AnswerResult result = exercise.checkAnswer("apple");
```

The no-argument DemoLessonCatalog uses English definitions for compatibility.
The desktop demo explicitly starts with Russian.
All modes use the same lesson and exercise ids. Progress can therefore be
combined across languages; track SourceLanguage separately if language-specific
statistics are desired.

## Integration for the session owner

LessonsPanel(catalog, onSelect) passes a selected Lesson to the callback.
LanguageLessonsPanel(initialLanguage, onSelect) passes both the selected-language
DemoLessonCatalog and Lesson. Keep this catalog in the session instead of creating
a new no-argument catalog, which would switch back to English definitions.
FlashcardsPanel(catalog.wordsForLesson(lesson.id())) displays the English word
and reveals its source-language meaning with "Show meaning".
Create and update Swing components on the EDT.
The facade owns navigation, completion, scoring, and duplicate-submission checks.
Exercises are stateless: calling checkAnswer again is allowed and awards no points.
Decorators can wrap Exercise while preserving its id.
findById returns Optional.empty() for an unknown id; wordsForLesson throws
IllegalArgumentException for an unknown id.

## Integration for the progress owner

Use AnswerResult.correct(), lesson.id(), and exercise.id().
Strategy must return a new list rather than mutate lesson.exercises().
The facade should publish an event once per accepted answer, including the ids
and AnswerResult. Observer, Strategy, statistics, and persistence belong to the
progress module and are not implemented here.

## Files

src/main/java/com/fluento/lessons/: implementation, panels, demos.
src/main/resources/lessons/: vocabulary_en.properties, vocabulary_ru.properties,
vocabulary_kk.properties. Keep the same keys in each file and use UTF-8.
src/test/java/com/fluento/lessons/: unit, integration, and Swing interaction tests.
pom.xml: JUnit 5 and pinned compiler/test plugins.

## Git

Review changes, create or use your feature/exercises-lessons branch, and stage
only your files. Existing edits in app/Main.java and .idea/misc.xml are separate.

```powershell
git add pom.xml src/main/java/com/fluento/lessons/ src/main/resources/lessons/ src/test/java/com/fluento/lessons/ docs/LESSONS_GUIDE.md docs/lessons-architecture.md
git diff --cached --stat
git commit -m "Implement multilingual lesson factories, builder and flashcards"
git push -u origin feature/exercises-lessons
```

Nothing is committed or pushed automatically.
