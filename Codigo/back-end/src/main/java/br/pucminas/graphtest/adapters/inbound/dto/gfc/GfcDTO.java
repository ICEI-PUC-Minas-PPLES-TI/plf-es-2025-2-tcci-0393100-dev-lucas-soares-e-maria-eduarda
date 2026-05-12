package br.pucminas.graphtest.adapters.inbound.dto.gfc;

import java.util.List;
import java.util.UUID;

/**
 * Response com a representacao completa de um Grafo de Fluxo de Controle.
 */
public record GfcDTO(
        UUID id,
        UUID projectId,
        UUID sourceFileId,
        String methodSignature,
        String name,
        String description,
        String language,
        List<GfcNodeDTO> nodes,
        List<GfcEdgeDTO> edges
) {
}
