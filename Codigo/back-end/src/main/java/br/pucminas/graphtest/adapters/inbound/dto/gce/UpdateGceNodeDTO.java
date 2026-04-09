package br.pucminas.graphtest.adapters.inbound.dto.gce;

import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * DTO HTTP para atualizar um no existente do GCE.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(UpdateGceNodeDTO.UpdateGceNodeLabelDTO.class),
        @JsonSubTypes.Type(UpdateGceNodeDTO.UpdateGceNodeOperatorDTO.class)
})
public sealed interface UpdateGceNodeDTO permits UpdateGceNodeDTO.UpdateGceNodeLabelDTO, UpdateGceNodeDTO.UpdateGceNodeOperatorDTO {
    record UpdateGceNodeLabelDTO(
            String label
    ) implements UpdateGceNodeDTO {
    }

    record UpdateGceNodeOperatorDTO(
            GceOperatorTypeEnum operatorType
    ) implements UpdateGceNodeDTO {
    }
}
