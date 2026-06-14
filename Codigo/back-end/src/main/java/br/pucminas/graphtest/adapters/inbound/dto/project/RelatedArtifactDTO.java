package br.pucminas.graphtest.adapters.inbound.dto.project;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

import java.util.UUID;

@Builder
@JsonPropertyOrder({"type", "id", "name"})
public record RelatedArtifactDTO(
        String type,
        UUID id,
        String name
) {
}
