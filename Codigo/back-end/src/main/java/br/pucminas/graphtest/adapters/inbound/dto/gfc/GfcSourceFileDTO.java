package br.pucminas.graphtest.adapters.inbound.dto.gfc;

import java.time.LocalDateTime;
import java.util.UUID;

public record GfcSourceFileDTO(
        UUID id,
        UUID projectId,
        String fileName,
        String language,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
