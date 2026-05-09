package br.pucminas.graphtest.adapters.inbound.dto.gfc;

import java.util.List;
import java.util.UUID;

/**
 * Response com a representacao completa de um Grafo de Fluxo de Controle.
 */
public record GfcDTO(
        UUID id,
        UUID projectId,
        String name,
        String description,
        String sourceCode,
        String language,
        List<GfcNodeDTO> nodes,
        List<GfcEdgeDTO> edges
) {
}
