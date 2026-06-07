package br.pucminas.graphtest.application.port.input.decisiontable.records;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableCellValueEnum;

import java.util.UUID;

public record FunctionalTestActionOutput(
        UUID actionId,
        String code,
        String label,
        DecisionTableCellValueEnum value
) {
}
