package br.pucminas.graphtest.application.port.input.project.records;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectArtifactOutput(
        UUID id,
        String type,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        RelatedArtifactOutput relatedArtifact
) {
}
