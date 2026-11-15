# Fluento

English practice with six coordinated design patterns. Java 21, Maven, Swing, JUnit 5.



## Run the prepared application

1. Install Java 21 or newer.

2. Extract the complete ZIP.

3. On Windows, double-click START-FLUENTO.cmd.

4. Alternatively: `java -jar dist/fluento.jar`.



The application has no runtime dependencies outside the JDK and works offline.

A graphical desktop is required. Java is not included in the archive.



## Build from source

Use JDK 21 and Maven 3.9+ (also verified with Maven 4.0.0-rc-3).

```text

mvn clean verify

java -jar target/fluento-1.0-SNAPSHOT.jar

```

The first build needs Internet access to download Maven plugins and JUnit.

In IntelliJ: open pom.xml, choose JDK 21, reload Maven, then run com.fluento.app.Main.

The old LessonsDemo entry point delegates to the same integrated application.



## Use

Select Russian, Kazakh, or English definitions as the source language.

Select Sequential Review or Weak Words First before starting a topic.

Each topic has four typed answers and four multiple-choice answers.

Use Show hint for a first-letter/length clue; check the answer, then advance.

Use Word cards to reveal vocabulary meanings.

All lessons returns to the dashboard; Continue resumes the most recently started unfinished session.

Start always creates a new round. Choosing a different topic replaces the resumable session.

For weak-word review, keep the app open, return to All lessons, choose Weak Words First, and start again.



## Six patterns

| Pattern | Main participants | Live use |

|---|---|---|

| Factory Method | ExerciseCreator, concrete creators, Exercise | Catalog creates two exercise types |

| Builder | LessonBuilder, Lesson | Catalog assembles valid, immutable lessons |

| Strategy | ReviewStrategy, SequentialReviewStrategy, WeakWordsFirstStrategy, ReviewSession | Facade chooses round ordering |

| Observer | ProgressTracker, ProgressObserver, ProgressSnapshot | Answers update the UI and StatisticsObserver |

| Decorator | HintDecorator, FeedbackDecorator, Exercise | Both decorators wrap catalog exercises |

| Facade | LessonFacade | UI submits answers and advances through a single session API |



## Scope and contracts

- 16 topics, 112 words, 224 exercises per source language. UI labels are English.

- Progress is in memory for the current application window; closing it resets progress.

- Statistics count attempts across rounds, not unique mastered words.

- Mistakes are lifetime counts within the window, keyed by exercise ID. Correct answers do not erase earlier mistakes.

- Translation and choice exercises have distinct IDs. Progress is shared across source languages.

- Weak Words First is error-count ordering, not a timed spaced-repetition scheduler.

- Order is fixed at the start of a round. Changing settings affects the next new round.

- All application state and observer callbacks run on the Swing event-dispatch thread.

- Observer RuntimeExceptions are reported and isolated; nested notifications are queued in chronological order.

  Observers must terminate and must not generate an unbounded chain of answers.

- No accounts, network, database, reminders, fuzzy spelling or disk persistence are implemented.



## Verification and documentation

- `mvn clean verify`: JUnit and JaCoCo, reports in target/surefire-reports and target/site/jacoco.

- Recreate actual Swing-component screen captures:

  `mvn test -Dfluento.captureDir=docs/screenshots`

- docs/Final_Report.pdf: report with UML, application screens, code excerpts and validation.

- docs/DEFENSE_RU.md: Russian defense and demonstration guide.

- docs/HANDOFF_RU.md: safe GitHub import and team workflow.

- docs/PROVENANCE.md: source commits and assisted integration changes.

- docs/VERIFICATION.md: measured results for this delivery.

- docs/uml/: editable PlantUML diagrams.



## Attribution

Original team commits are identified in docs/PROVENANCE.md.

This local delivery includes AI-assisted integration, fixes, tests, UI and documentation.

It does not create commits on behalf of students or establish individual authorship.

Each participant must review and understand their implementation and follow course disclosure rules.




## Updated learning controls
Word cards open first. British English IPA is shown for every built-in word.
Use Start practice (Alt+P) when ready. Cards: Left/Right to move; Space to reveal/hide.
Practice: Enter checks or advances; Alt+H toggles the hint; Alt+S skips;
Alt+1 through Alt+4 select a multiple-choice button. Tab and Space also work on buttons.
A skip reveals the solution, counts as incorrect, and is included in weak review.
Restart round clears only round score and position; cumulative attempts and topic progress remain.
Topic bars count unique exercise IDs answered correctly at least once (0–8), shared across source languages.
Unknown pronunciation IDs have no transcription rather than an invented one.
New rounds replace the current resumable round. Closing the window resets all progress.
IPA is a common British pronunciation; regional alternatives exist.
Reference convention: https://dictionary.cambridge.org/help/phonetics.html

## Expanded catalog and search
16 topics, 112 words and 224 exercises per source language. Search is case-insensitive and matches topic titles or English words. Clear the search field to restore all topics. The four original lessons have eight questions each; the twelve new topics have sixteen.
