package br.pucminas.graphtest.application.port.input.gce.records;

import br.pucminas.graphtest.application.domain.enums.GceEdgeTypeEnum;

/**
 * Dados de entrada de uma aresta do GCE.
 */
public record GceEdgeInput(
        String sourceNodeCode,
        String targetNodeCode,
        GceEdgeTypeEnum type
) {
}
