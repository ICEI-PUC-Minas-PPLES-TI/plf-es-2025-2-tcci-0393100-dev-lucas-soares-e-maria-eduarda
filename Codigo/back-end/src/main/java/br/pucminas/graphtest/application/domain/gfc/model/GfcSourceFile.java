package br.pucminas.graphtest.application.domain.gfc.model;

import br.pucminas.graphtest.application.domain.shared.model.BaseEntity;
import br.pucminas.graphtest.application.exception.InvalidGfcModelException;
import br.pucminas.graphtest.application.exception.JavaSourceFileException;

import java.time.LocalDateTime;
import java.util.UUID;

import static br.pucminas.graphtest.application.domain.gfc.rules.GfcDomainRules.JAVA_LANGUAGE;
import static br.pucminas.graphtest.application.domain.gfc.rules.GfcDomainRules.normalizeJavaLanguage;

/**
 * Entidade de dominio que representa um arquivo-fonte Java cadastrado para a feature GFC.
 */
public class GfcSourceFile extends BaseEntity {

    private UUID projectId;
    private String fileName;
    private String content;
    private String language;

    public GfcSourceFile(UUID id, UUID projectId, String fileName, String content, String language) {
        this(id, projectId, fileName, content, language, null, null);
    }

    public GfcSourceFile(UUID id,
                         UUID projectId,
                         String fileName,
                         String content,
                         String language,
                         LocalDateTime createdAt,
                         LocalDateTime updatedAt) {
        this.id = id == null ? UUID.randomUUID() : id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.projectId = requireProjectId(projectId);
        this.fileName = requireJavaFileName(fileName);
        this.content = requireContent(content);
        this.language = requireJavaLanguage(language);
    }

    public static GfcSourceFile create(UUID projectId, String fileName, String content) {
        return new GfcSourceFile(null, projectId, fileName, content, JAVA_LANGUAGE);
    }

    private UUID requireProjectId(UUID value) {
        if (value == null) {
            throw new JavaSourceFileException("O projeto e obrigatorio para cadastrar o arquivo Java.");
        }
        return value;
    }

    private String requireJavaFileName(String value) {
        if (value == null || value.isBlank()) {
            throw new JavaSourceFileException("O nome do arquivo Java e obrigatorio.");
        }

        String normalized = value.trim();
        if (!normalized.toLowerCase().endsWith(".java")) {
            throw new JavaSourceFileException("O arquivo enviado deve possuir extensao .java.");
        }
        return normalized;
    }

    private String requireContent(String value) {
        if (value == null || value.isBlank()) {
            throw new JavaSourceFileException("O arquivo Java enviado esta vazio.");
        }
        return value;
    }

    private String requireJavaLanguage(String value) {
        try {
            return normalizeJavaLanguage(value);
        } catch (InvalidGfcModelException exception) {
            throw new JavaSourceFileException("A linguagem suportada para GFC e Java.");
        }
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContent() {
        return content;
    }

    public String getLanguage() {
        return language;
    }
}
