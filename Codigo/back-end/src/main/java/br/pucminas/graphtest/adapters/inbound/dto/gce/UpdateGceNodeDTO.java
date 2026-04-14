package br.pucminas.graphtest.adapters.inbound.dto.gce;

import br.pucminas.graphtest.application.domain.gce.enums.GceOperatorTypeEnum;

public record UpdateGceNodeDTO(
        String label,
        GceOperatorTypeEnum operatorType
) {
}
