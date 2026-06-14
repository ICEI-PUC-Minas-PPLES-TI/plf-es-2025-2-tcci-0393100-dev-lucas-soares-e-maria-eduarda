package br.pucminas.graphtest.application.service.gfc.builder;

import br.pucminas.graphtest.application.domain.gfc.model.GfcEdge;
import br.pucminas.graphtest.application.domain.gfc.model.GfcNode;

import java.util.List;

/**
 * Resultado da construcao inicial dos elementos de um Grafo de Fluxo de Controle.
 *
 * @param nodes nos gerados para o GFC
 * @param edges arestas geradas para o GFC
 */
public record GfcControlFlowBuildResult(
        List<GfcNode> nodes,
        List<GfcEdge> edges
) {
}
