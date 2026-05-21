package br.pucminas.graphtest.application.domain.gfc.rules;

import br.pucminas.graphtest.application.exception.InvalidGfcModelException;

import java.util.UUID;

/**
 * Regras e normalizacoes compartilhadas pelas entidades do dominio GFC.
 */
public final class GfcDomainRules {

    public static final String JAVA_LANGUAGE = "Java";

    private GfcDomainRules() {
    }

    public static UUID requireUuid(UUID value, String fieldDisplayName) {
        if (value == null) {
            throw new InvalidGfcModelException(fieldDisplayName + " e obrigatorio.");
        }
        return value;
    }

    public static String requireText(String value, String fieldDisplayName) {
        if (value == null || value.isBlank()) {
            throw new InvalidGfcModelException(fieldDisplayName + " e obrigatorio.");
        }
        return value.trim();
    }

    public static String normalizeOptionalText(String value) {
        return value == null ? "" : value.trim();
    }

    public static String normalizeJavaLanguage(String value) {
        String normalized = requireText(value, "A linguagem");
        if (!JAVA_LANGUAGE.equalsIgnoreCase(normalized)) {
            throw new InvalidGfcModelException("A linguagem suportada para GFC e Java.");
        }
        return JAVA_LANGUAGE;
    }

    public static Integer requirePositiveLine(Integer value, String fieldDisplayName) {
        if (value == null) {
            throw new InvalidGfcModelException(fieldDisplayName + " e obrigatoria para nos de codigo-fonte.");
        }
        if (value < 1) {
            throw new InvalidGfcModelException(fieldDisplayName + " deve ser maior que zero.");
        }
        return value;
    }

    public static <T> T requireNonNull(T value, String message) {
        if (value == null) {
            throw new InvalidGfcModelException(message);
        }
        return value;
    }
}
