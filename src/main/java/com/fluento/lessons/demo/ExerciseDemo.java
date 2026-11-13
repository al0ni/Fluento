package com.fluento.lessons.demo;

import com.fluento.lessons.catalog.DemoLessonCatalog;
import com.fluento.lessons.catalog.SourceLanguage;
import com.fluento.model.Exercise;
import java.util.Scanner;

public final class ExerciseDemo {
    private ExerciseDemo() {}

    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in)) {
            System.out.println("Learn English from: 1 - Russian, 2 - Kazakh, 3 - English definitions");
            if (!input.hasNextLine()) return;
            SourceLanguage language = switch (input.nextLine().strip()) {
                case "1" -> SourceLanguage.RUSSIAN;
                case "2" -> SourceLanguage.KAZAKH;
                case "3" -> SourceLanguage.ENGLISH;
                default -> null;
            };
            if (language == null) {
                System.out.println("Please restart and select 1, 2, or 3.");
                return;
            }
            var lesson = new DemoLessonCatalog(language).findById("food").orElseThrow();
            System.out.println("Fluento: " + lesson.title());
            for (Exercise exercise : lesson.exercises()) {
                System.out.println(exercise.view().prompt());
                if (!exercise.view().options().isEmpty()) {
                    System.out.println(exercise.view().options());
                    System.out.println("Enter the text of your chosen option:");
                }
                if (!input.hasNextLine()) {
                    return;
                }
                System.out.println(exercise.checkAnswer(input.nextLine()).feedback());
            }
        }
    }
}
