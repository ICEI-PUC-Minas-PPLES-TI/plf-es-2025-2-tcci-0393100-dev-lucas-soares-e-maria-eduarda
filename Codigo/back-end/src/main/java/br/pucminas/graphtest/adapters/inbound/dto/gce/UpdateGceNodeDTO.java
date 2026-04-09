package br.pucminas.graphtest.adapters.inbound.dto.gce;

import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;

/**
 * DTO HTTP para atualizar um no existente do GCE.
 */
public record UpdateGceNodeDTO(
        String label,
        GceOperatorTypeEnum operatorType
) {
}
