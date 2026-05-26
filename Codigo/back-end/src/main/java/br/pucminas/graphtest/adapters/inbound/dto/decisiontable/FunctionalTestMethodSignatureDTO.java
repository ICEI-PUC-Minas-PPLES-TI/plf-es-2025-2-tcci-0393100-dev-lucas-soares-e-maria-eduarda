package br.pucminas.graphtest.adapters.inbound.dto.decisiontable;

import java.util.List;
import java.util.UUID;

public record FunctionalTestMethodSignatureDTO(
        UUID ruleId,
        String ruleCode,
        String methodName,
        List<FunctionalTestConditionDTO> conditions,
        List<FunctionalTestActionDTO> actions,
        String generatedCode
) {
}
