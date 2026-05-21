package br.pucminas.graphtest.application.port.input.gfc.records;

import br.pucminas.graphtest.application.domain.gfc.model.GfcSourceFile;

import java.time.LocalDateTime;
import java.util.UUID;

public record GfcSourceFileOutput(
        UUID id,
        UUID projectId,
        String fileName,
        String language,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static GfcSourceFileOutput from(GfcSourceFile sourceFile) {
        return new GfcSourceFileOutput(
                sourceFile.getId(),
                sourceFile.getProjectId(),
                sourceFile.getFileName(),
                sourceFile.getLanguage(),
                sourceFile.getCreatedAt(),
                sourceFile.getUpdatedAt()
        );
    }
}
