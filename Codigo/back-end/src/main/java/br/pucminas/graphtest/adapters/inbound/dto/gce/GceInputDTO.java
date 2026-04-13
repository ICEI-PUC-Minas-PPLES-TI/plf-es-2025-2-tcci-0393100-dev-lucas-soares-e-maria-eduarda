package br.pucminas.graphtest.adapters.inbound.dto.gce;

import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.RestrictionTypeEnum;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

/**
 * DTO de entrada do GCE para criacao e validacao.
 */
@Builder
public record GceInputDTO(
        UUID projectId,
        String name,
        String description,
        Boolean selected,
        List<GceNodeInputDTO> nodes,
        List<GceEdgeInputDTO> edges,
        List<GceRestrictionInputDTO> restrictions
) {

    /**
     * DTO de entrada de no do GCE.
     */
    public record GceNodeInputDTO(
            String code,
            String label,
            GceNodeTypeEnum type,
            GceOperatorTypeEnum operatorType,
            List<String> sourceNodeCodes,
            List<String> targetNodeCodes
    ) {

        public GceNodeInputDTO(
                String code,
                String label,
                GceNodeTypeEnum type,
                GceOperatorTypeEnum operatorType
        ) {
            this(code, label, type, operatorType, List.of(), List.of());
        }
    }

    /**
     * DTO de entrada de aresta do GCE referenciando nos por codigo.
     */
    public record GceEdgeInputDTO(
            String sourceNodeCode,
            String targetNodeCode,
            GceEdgeTypeEnum type
    ) {
    }

    /**
     * DTO de entrada de restricao do GCE referenciando nos por codigo.
     */
    public record GceRestrictionInputDTO(
            RestrictionTypeEnum type,
            List<String> nodeCodes
    ) {
    }
}
