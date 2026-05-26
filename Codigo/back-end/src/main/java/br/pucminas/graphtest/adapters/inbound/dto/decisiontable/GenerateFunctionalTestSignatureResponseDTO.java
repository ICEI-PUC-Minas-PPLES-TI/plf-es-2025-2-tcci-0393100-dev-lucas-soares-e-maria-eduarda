package br.pucminas.graphtest.adapters.inbound.dto.decisiontable;

import java.util.List;
import java.util.UUID;

public record GenerateFunctionalTestSignatureResponseDTO(
        UUID decisionTableId,
        UUID gceId,
        UUID projectId,
        String decisionTableName,
        int rulesCount,
        List<FunctionalTestMethodSignatureDTO> testMethods,
        String generatedCode,
        List<String> warnings
) {
}
