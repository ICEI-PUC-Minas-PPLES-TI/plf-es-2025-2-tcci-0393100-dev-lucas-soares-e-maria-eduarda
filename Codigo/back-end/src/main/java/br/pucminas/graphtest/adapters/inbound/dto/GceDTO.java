package br.pucminas.graphtest.adapters.inbound.dto;

import br.pucminas.graphtest.application.domain.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.domain.enums.GceOperatorTypeEnum;
import br.pucminas.graphtest.application.domain.enums.RestrictionTypeEnum;
import lombok.Builder;

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
            GceOperatorTypeEnum operatorType
    ) {
    }

    /**
     * DTO de aresta do GCE.
     */
    public record GceEdgeDTO(
            UUID id,
            UUID sourceNodeId,
            UUID targetNodeId,
            GceEdgeTypeEnum type
    ) {
    }

    /**
     * DTO de restricao do GCE.
     */
    public record GceRestrictionDTO(
            UUID id,
            RestrictionTypeEnum type,
            List<UUID> nodeIds
    ) {
    }
}
