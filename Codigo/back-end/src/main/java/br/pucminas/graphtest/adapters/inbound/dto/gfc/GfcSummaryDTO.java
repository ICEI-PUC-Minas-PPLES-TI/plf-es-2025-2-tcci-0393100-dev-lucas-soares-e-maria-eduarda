package br.pucminas.graphtest.adapters.inbound.dto.gfc;

import java.util.UUID;

/**
 * Response resumida de um GFC para listagem.
 */
public record GfcSummaryDTO(
        UUID id,
        UUID projectId,
        UUID sourceFileId,
        String methodSignature,
        String name,
        String description,
        String language
) {
}
