package br.pucminas.graphtest.adapters.inbound.dto.gce;

import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.RestrictionTypeEnum;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
    @JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    @JsonSubTypes({
            @JsonSubTypes.Type(GceLabeledNodeInputDTO.class),
            @JsonSubTypes.Type(GceOperatorNodeInputDTO.class)
    })
    public sealed interface GceNodeInputDTO permits GceLabeledNodeInputDTO, GceOperatorNodeInputDTO {
        String code();
        GceNodeTypeEnum type();
    }

    public record GceLabeledNodeInputDTO(
            String code,
            String label,
            GceNodeTypeEnum type
    ) implements GceNodeInputDTO {
    }

    public record GceOperatorNodeInputDTO(
            String code,
            GceNodeTypeEnum type,
            GceOperatorTypeEnum operatorType,
            List<String> sourceNodeCodes,
            List<String> targetNodeCodes
    ) implements GceNodeInputDTO {

        public GceOperatorNodeInputDTO(
                String code,
                GceNodeTypeEnum type,
                GceOperatorTypeEnum operatorType
        ) {
            this(code, type, operatorType, List.of(), List.of());
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
