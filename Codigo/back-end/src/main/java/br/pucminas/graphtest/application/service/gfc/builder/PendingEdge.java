package br.pucminas.graphtest.application.service.gfc.builder;

import br.pucminas.graphtest.application.domain.gfc.enums.GfcEdgeTypeEnum;

/**
 * Estado auxiliar de uma aresta ainda nao materializada.
 *
 * @param sourceNodeCode codigo do no de origem
 * @param type tipo da aresta a criar
 * @param label rotulo opcional da aresta
 */
public record PendingEdge(
        String sourceNodeCode,
        GfcEdgeTypeEnum type,
        String label
) {
}
