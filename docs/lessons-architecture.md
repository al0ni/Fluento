# Lesson architecture

```mermaid
classDiagram
    Exercise <|.. TranslationExercise
    Exercise <|.. MultipleChoiceExercise
    ExerciseCreator <|-- TranslationExerciseCreator
    ExerciseCreator <|-- MultipleChoiceExerciseCreator
    TranslationExerciseCreator ..> TranslationExercise : creates
    MultipleChoiceExerciseCreator ..> MultipleChoiceExercise : creates
    LessonBuilder ..> Lesson : builds
    Lesson o-- Exercise
    DemoLessonCatalog ..> LessonBuilder : uses
    DemoLessonCatalog ..> ExerciseCreator : uses
    DemoLessonCatalog ..> SourceLanguage : loads vocabulary
    LessonCatalog <|.. DemoLessonCatalog
    LanguageLessonsPanel ..> DemoLessonCatalog : creates for selected language
    LessonsPanel ..> LessonCatalog : reads
    FlashcardsPanel ..> Word : displays
```

Factory Method creates exercises; Builder assembles them into lessons.
SourceLanguage selects a UTF-8 vocabulary resource without modifying shared models.
The session and progress modules remain the other team members' responsibility.
