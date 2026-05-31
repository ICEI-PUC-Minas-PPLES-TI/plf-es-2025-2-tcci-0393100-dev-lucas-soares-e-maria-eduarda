package br.pucminas.graphtest.adapters.inbound.dto.project;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@JsonPropertyOrder({"id", "type", "name", "createdAt", "updatedAt", "relatedArtifact"})
public record ProjectArtifactDTO(
        UUID id,
        String type,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        RelatedArtifactDTO relatedArtifact
) {
}
