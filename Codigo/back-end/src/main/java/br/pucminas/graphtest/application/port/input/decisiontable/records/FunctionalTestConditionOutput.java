package br.pucminas.graphtest.application.port.input.decisiontable.records;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableConditionValueEnum;

import java.util.UUID;

public record FunctionalTestConditionOutput(
        UUID conditionId,
        String code,
        String label,
        DecisionTableConditionValueEnum value
) {
}
