package br.pucminas.graphtest.application.port.input.decisiontable.records;

import java.util.List;
import java.util.UUID;

public record FunctionalTestMethodSignatureOutput(
        UUID ruleId,
        String ruleCode,
        String methodName,
        List<FunctionalTestConditionOutput> conditions,
        List<FunctionalTestActionOutput> actions,
        String generatedCode
) {
}
