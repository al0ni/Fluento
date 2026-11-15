# Integrated architecture
The original Factory Method and Builder module is retained and connected to the
session, review, progress and decorator modules.

```mermaid
flowchart LR
  Catalog[DemoLessonCatalog] --> Factory[ExerciseCreator]
  Factory --> Exercise[Exercise]
  Catalog --> Builder[LessonBuilder]
  Builder --> Lesson[Lesson]
  Catalog --> Decorators[HintDecorator + FeedbackDecorator]
  Decorators --> Exercise
  UI[FluentoPanel / LessonSessionPanel] --> Facade[LessonFacade]
  Facade --> Lesson
  Facade --> Context[ReviewSession]
  Context --> Strategy[ReviewStrategy]
  Facade --> Exercise
  Facade --> Tracker[ProgressTracker]
  Tracker --> Observers[ProgressObserver subscribers]
  Observers --> UI
```
See docs/uml for UML class and sequence diagrams with explicit relationship notation.
