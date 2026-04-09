package br.pucminas.graphtest.adapters.inbound.dto.gce;

import br.pucminas.graphtest.application.domain.gce.enums.GceNodeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

/**
 * DTO HTTP para adicionar um novo no a um GCE existente.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(AddGceNodeDTO.AddLabeledGceNodeDTO.class),
        @JsonSubTypes.Type(AddGceNodeDTO.AddOperatorGceNodeDTO.class)
})
public sealed interface AddGceNodeDTO permits AddGceNodeDTO.AddLabeledGceNodeDTO, AddGceNodeDTO.AddOperatorGceNodeDTO {
    String code();
    GceNodeTypeEnum type();

    record AddLabeledGceNodeDTO(
            String code,
            String label,
            GceNodeTypeEnum type
    ) implements AddGceNodeDTO {
    }

    record AddOperatorGceNodeDTO(
            String code,
            GceNodeTypeEnum type,
            GceOperatorTypeEnum operatorType,
            List<String> sourceNodeCodes,
            List<String> targetNodeCodes
    ) implements AddGceNodeDTO {
    }
}
