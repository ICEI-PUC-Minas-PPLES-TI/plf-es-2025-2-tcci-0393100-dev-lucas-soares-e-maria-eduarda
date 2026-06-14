package br.pucminas.graphtest.application.port.input.gce.records;

import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import br.pucminas.graphtest.application.domain.gce.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Saida de um no do GCE.
 */
public record GceNodeOutput(
        UUID id,
        String code,
        String label,
        GceNodeTypeEnum type,
        GceOperatorTypeEnum operatorType,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    /**
     * Converte um no de dominio em sua representacao de saida.
     *
     * @param node no do dominio
     * @return saida correspondente
     */
    public static GceNodeOutput from(GceNode node) {
        return new GceNodeOutput(
                node.getId(),
                node.getCode(),
                node.getLabel(),
                node.getType(),
                node.getOperatorType(),
                node.getCreatedAt(),
                normalizeUpdatedAt(node.getCreatedAt(), node.getUpdatedAt())
        );
    }

    private static LocalDateTime normalizeUpdatedAt(LocalDateTime createdAt, LocalDateTime updatedAt) {
        return updatedAt != null && updatedAt.equals(createdAt) ? null : updatedAt;
    }
}
