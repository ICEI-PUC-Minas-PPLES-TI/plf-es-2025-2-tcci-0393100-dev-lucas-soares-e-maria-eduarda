package br.pucminas.graphtest.adapters.inbound.dto.decisiontable;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableCellValueEnum;

import java.util.UUID;

public record FunctionalTestActionDTO(
        UUID actionId,
        String code,
        String label,
        DecisionTableCellValueEnum value
) {
}
