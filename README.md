# Fluento

Fluento is our desktop application for learning English vocabulary. We built it with Java 21 and Swing and integrated six design patterns into one learning workflow.

The application includes **16 topics, 112 words and 224 exercises per source-language mode**. Learners can study from Russian translations, Kazakh translations or English definitions.

## Team

**Group: SE-2509**

| Team member | Assigned patterns | Main responsibility |
|---|---|---|
| Rakhimzhanova Alua | Factory Method, Builder | Exercise creation and lesson assembly |
| Assiya Aitbayeva | Strategy, Observer | Review order and progress tracking |
| Moldir Yerzhan | Decorator, Facade | Exercise enhancements and session control |

## Features

- Search topics by title or English vocabulary.
- Study word cards before starting practice.
- View a common British IPA transcription for every built-in word.
- Reveal and hide word meanings.
- Complete typed-answer and multiple-choice exercises.
- Show or hide hints before submitting an answer.
- Skip unknown answers and view feedback.
- Repeat exercises using Sequential Review or Weak Words First.
- Track round score, overall accuracy and progress for each topic.
- Restart a round while retaining cumulative progress.
- Use keyboard shortcuts throughout the learning session.

## Topics

Basics, Food, Travel, Daily Life, Family, Animals, Colours, Numbers, Weather, Clothes, School, City, Body, Nature, Actions and Feelings.

The first four topics contain four words and eight exercises each. The remaining twelve contain eight words and sixteen exercises each. Every word has one typed-answer exercise and one multiple-choice exercise.

## Technologies

- Java 21
- Swing
- Maven
- JUnit Jupiter 5.11.4
- JaCoCo 0.8.12
- GitHub Actions

## Run the application

### Prepared build

Install Java 21 or newer and extract the complete application archive.

On Windows, run:

```text
START-FLUENTO.cmd
```

Alternatively:

```bash
java -jar dist/fluento.jar
```

The prepared application works offline and requires a graphical desktop. Java is not included in the archive.

### Build from source

Use JDK 21 and Maven 3.9 or newer.

```bash
mvn clean verify
java -jar target/fluento-1.0-SNAPSHOT.jar
```

The first build requires Internet access to download dependencies and build plugins.

On Windows, `BUILD-FLUENTO.cmd` runs verification and copies the updated application into `dist/fluento.jar`.

### IntelliJ IDEA

1. Open the project through `pom.xml`.
2. Set Project SDK and Module SDK to Java 21.
3. Reload the Maven project.
4. Run `com.fluento.app.Main`.

## Learning workflow

1. Select a source language and review strategy.
2. Choose a topic or find one through search.
3. Study its word cards and reveal meanings when needed.
4. Select **Start practice**.
5. Type an English answer or select an answer button.
6. Check the answer, read the feedback and continue.
7. Finish the round or restart it for another attempt.

**All lessons** returns to the dashboard. **Continue** reopens the most recently started unfinished session. Starting a new topic creates a new round and replaces the resumable session.

A skipped exercise counts as an incorrect attempt and becomes eligible for weak-exercise review. After submission, the answer cannot be submitted again and the hint is hidden.

## Keyboard shortcuts

| Context | Shortcut | Action |
|---|---|---|
| Word cards | Left / Right | Previous / next word |
| Word cards | Space | Show / hide meaning |
| Word cards | Alt+P | Open practice |
| Practice | Enter | Check answer / continue |
| Practice | Alt+H | Show / hide hint |
| Practice | Alt+S | Skip the exercise |
| Practice | Alt+1–4 | Select an answer option |
| Controls | Tab | Move keyboard focus |
| Focused button | Space | Activate the button |

## Design patterns

| Pattern | Main classes | Application |
|---|---|---|
| Factory Method | `ExerciseCreator`, `TranslationExerciseCreator`, `MultipleChoiceExerciseCreator` | Creates exercises through a shared creator contract |
| Builder | `LessonBuilder`, `Lesson` | Assembles and validates lessons |
| Strategy | `ReviewStrategy`, `SequentialReviewStrategy`, `WeakWordsFirstStrategy`, `ReviewSession` | Selects the exercise order |
| Observer | `ProgressTracker`, `ProgressObserver`, `ProgressSnapshot` | Publishes progress updates to subscribers |
| Decorator | `HintDecorator`, `FeedbackDecorator` | Adds hints and feedback while preserving the exercise contract |
| Facade | `LessonFacade` | Coordinates answer checking, progression, score and progress |

Our catalog creates exercises through Factory Method, decorates them and assembles lessons through Builder. Strategy determines the round order. Facade controls the session, and Observer distributes progress updates.

## Project structure

```text
src/
├── main/
│   ├── java/com/fluento/
│   │   ├── app/
│   │   ├── model/
│   │   ├── lessons/
│   │   │   ├── builder/
│   │   │   ├── catalog/
│   │   │   ├── demo/
│   │   │   ├── factory/
│   │   │   └── ui/
│   │   ├── progress/
│   │   ├── review/
│   │   └── session/
│   └── resources/lessons/
└── test/java/com/fluento/

docs/
├── screenshots/
└── uml/
```

## Progress and review rules

We track three separate measures:

- **Round score:** correct answers in the current round.
- **Overall statistics:** correct and incorrect attempts across rounds.
- **Topic progress:** unique exercises answered correctly at least once, out of eight or sixteen.

Repeated correct answers do not increase unique topic progress. Restarting resets round position and score while retaining cumulative statistics.

Weak Words First sorts exercises by accumulated mistake count. Exercises with equal counts retain their original order. Correct answers do not erase earlier mistakes.

Translation and multiple-choice exercises have distinct identifiers. Source-language modes share identifiers and therefore share progress.

All progress remains in memory until the application window closes.

## Validation and error handling

We validate lesson fields, exercise identifiers, vocabulary and answer options. The session prevents blank submissions, duplicate answers, skipping answered exercises and advancing beyond a completed round.

Progress snapshots and lesson lists use defensive copies. Observer failures are reported without blocking other subscribers, and nested notifications are delivered in chronological order.

Application state and observer callbacks are designed to run on the Swing event-dispatch thread.

## Tests

Run the complete verification:

```bash
mvn clean verify
```

Our final local verification completed **913 tests with zero failures, errors or skipped tests**.

| Coverage measure | Result |
|---|---|
| Lines | 643 / 695 — 92.5% |
| Branches | 263 / 310 — 84.8% |

The suite includes exercise checks in all three source languages, review ordering, decorators, progress notifications, session boundaries, restart behavior, topic search and Swing workflows.

Reports are generated in:

```text
target/surefire-reports/
target/site/jacoco/
```

To regenerate application screenshots during the UI tests:

```bash
mvn test -Dfluento.captureDir=docs/screenshots
```

The repository includes a GitHub Actions workflow for Java 21 verification. The figures above describe our local test run.

## Documentation

- `docs/uml/` — editable UML diagrams.
- `docs/screenshots/` — application screenshots.
- `docs/VERIFICATION.md` — verification results.
## Limitations and further work

The current version does not include accounts, persistent storage, pronunciation audio or timed spaced repetition. Answer checking uses normalized exact English matches rather than synonyms or spelling correction.

IPA transcriptions represent common British pronunciations; regional variants exist. The notation follows the [Cambridge Dictionary phonetics guide](https://dictionary.cambridge.org/help/phonetics.html).

Our planned improvements include persistent learner profiles, progress export, a review schedule based on answer history, additional vocabulary, accepted answer alternatives and broader accessibility testing.