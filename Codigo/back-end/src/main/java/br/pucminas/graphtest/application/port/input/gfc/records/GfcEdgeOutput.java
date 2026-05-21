package br.pucminas.graphtest.application.port.input.gfc.records;

import br.pucminas.graphtest.application.domain.gfc.enums.GfcEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gfc.model.GfcEdge;

import java.util.UUID;

/**
 * Saida de uma aresta do GFC.
 */
public record GfcEdgeOutput(
        UUID id,
        String sourceNodeCode,
        String targetNodeCode,
        GfcEdgeTypeEnum type,
        String label
) {

    /**
     * Converte uma aresta de dominio em sua representacao de saida.
     *
     * @param edge aresta do dominio
     * @return saida correspondente
     */
    public static GfcEdgeOutput from(GfcEdge edge) {
        return new GfcEdgeOutput(
                edge.getId(),
                edge.getSourceNodeCode(),
                edge.getTargetNodeCode(),
                edge.getType(),
                edge.getLabel()
        );
    }
}
