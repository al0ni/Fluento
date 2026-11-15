package com.fluento.model;

public record AnswerResult(
        boolean correct,
        String feedback
) {
}
