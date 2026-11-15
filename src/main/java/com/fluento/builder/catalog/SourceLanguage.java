package com.fluento.builder.catalog;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public enum SourceLanguage {
    RUSSIAN("Russian", "ru"),
    KAZAKH("Kazakh", "kk"),
    ENGLISH("English definitions", "en");

    private final String label;
    private final String code;

    SourceLanguage(String label, String code) {
        this.label = label;
        this.code = code;
    }

    public Properties loadVocabulary() {
        String path = "/lessons/vocabulary_" + code + ".properties";
        try (InputStream stream = SourceLanguage.class.getResourceAsStream(path)) {
            if (stream == null) {
                throw new IllegalStateException("Missing vocabulary: " + path);
            }
            Properties vocabulary = new Properties();
            vocabulary.load(new InputStreamReader(stream, StandardCharsets.UTF_8));
            return vocabulary;
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot load vocabulary: " + path, exception);
        }
    }

    @Override
    public String toString() {
        return label;
    }
}
