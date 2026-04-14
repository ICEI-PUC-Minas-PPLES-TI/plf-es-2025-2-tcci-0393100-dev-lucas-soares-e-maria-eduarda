package br.pucminas.graphtest.application.port.input.gce.records;

import br.pucminas.graphtest.application.domain.gce.model.GceEdge;
import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Saida de uma aresta do GCE.
 */
public record GceEdgeOutput(
        UUID id,
        String sourceNodeCode,
        String targetNodeCode,
        GceEdgeTypeEnum type,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
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
                edge.getType(),
                edge.getCreatedAt(),
                normalizeUpdatedAt(edge.getCreatedAt(), edge.getUpdatedAt())
        );
    }

    private static LocalDateTime normalizeUpdatedAt(LocalDateTime createdAt, LocalDateTime updatedAt) {
        return updatedAt != null && updatedAt.equals(createdAt) ? null : updatedAt;
    }
}
