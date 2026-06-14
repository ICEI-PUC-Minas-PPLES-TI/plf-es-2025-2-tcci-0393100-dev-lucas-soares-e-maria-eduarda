package br.pucminas.graphtest.adapters.inbound.dto.gce;

import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.RestrictionTypeEnum;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de entrada e saida do GCE na camada HTTP.
 */
@Builder
public record GceDTO(
        UUID id,
        UUID projectId,
        String name,
        String description,
        Boolean selected,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<GceNodeDTO> nodes,
        List<GceEdgeDTO> edges,
        List<GceRestrictionDTO> restrictions
) {

    /**
     * DTO de no do GCE.
     */
    public record GceNodeDTO(
            UUID id,
            String code,
            String label,
            GceNodeTypeEnum type,
            GceOperatorTypeEnum operatorType,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }

    /**
     * DTO de aresta do GCE.
     */
    public record GceEdgeDTO(
            UUID id,
            String sourceNodeCode,
            String targetNodeCode,
            GceEdgeTypeEnum type,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }

    /**
     * DTO de restricao do GCE.
     */
    public record GceRestrictionDTO(
            UUID id,
            RestrictionTypeEnum type,
            List<String> nodeCodes,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }
}
