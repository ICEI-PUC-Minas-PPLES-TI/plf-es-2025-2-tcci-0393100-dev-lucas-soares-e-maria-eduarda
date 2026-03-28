package br.pucminas.graphtest.application.port.input.gce.records;

import br.pucminas.graphtest.application.domain.GceEdge;
import br.pucminas.graphtest.application.domain.enums.GceEdgeTypeEnum;

import java.util.UUID;

/**
 * Saida de uma aresta do GCE.
 */
public record GceEdgeOutput(
        UUID id,
        String sourceNodeCode,
        String targetNodeCode,
        GceEdgeTypeEnum type
) {

    /**
     * Converte uma aresta de dominio em sua representacao de saida.
     *
     * @param edge aresta do dominio
     * @return saida correspondente
     */
    public static GceEdgeOutput from(GceEdge edge) {
        return new GceEdgeOutput(
                edge.getId(),
                edge.getSourceNodeCode(),
                edge.getTargetNodeCode(),
                edge.getType()
        );
    }
}
