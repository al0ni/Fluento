# Lessons and exercises
The final application starts from com.fluento.Main.
LessonsDemo delegates to Main for backward compatibility.
See README.md for build, run, review and progress behavior.

The original four lessons have four words each. Twelve additional topics have eight words each. Every word has typed-answer and multiple-choice exercises. Russian, Kazakh and English-definition resources
are UTF-8 properties files. Answers are English words, not option numbers.

ExerciseCreator defines the factory method createExercise(Word). Its two concrete
creators produce TranslationExercise and MultipleChoiceExercise. LessonBuilder
assembles immutable lessons and rejects empty lessons, invalid fields and duplicate IDs.
DemoLessonCatalog coordinates creation and applies HintDecorator and FeedbackDecorator.

Word, Exercise, Lesson, ExerciseView and AnswerResult retain their shared signatures.
IDs are identical across source languages. Translation and choice prefixes distinguish
exercise types. The in-memory progress tracker therefore combines source languages
while tracking each exercise type separately.

For the complete six-pattern diagrams, see docs/uml and Final_Report.pdf.
