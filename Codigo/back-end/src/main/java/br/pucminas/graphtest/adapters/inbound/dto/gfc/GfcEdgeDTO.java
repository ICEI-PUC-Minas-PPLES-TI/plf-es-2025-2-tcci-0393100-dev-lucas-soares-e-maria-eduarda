package br.pucminas.graphtest.adapters.inbound.dto.gfc;

import br.pucminas.graphtest.application.domain.gfc.enums.GfcEdgeTypeEnum;

import java.util.UUID;

/**
 * Response de uma aresta do GFC.
 */
public record GfcEdgeDTO(
        UUID id,
        String sourceNodeCode,
        String targetNodeCode,
        GfcEdgeTypeEnum type,
        String label
) {
}
