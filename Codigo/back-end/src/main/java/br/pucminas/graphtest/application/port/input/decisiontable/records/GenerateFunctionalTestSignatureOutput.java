package br.pucminas.graphtest.application.port.input.decisiontable.records;

import java.util.List;
import java.util.UUID;

public record GenerateFunctionalTestSignatureOutput(
        UUID decisionTableId,
        UUID gceId,
        UUID projectId,
        String decisionTableName,
        int rulesCount,
        List<FunctionalTestMethodSignatureOutput> testMethods,
        String generatedCode,
        List<String> warnings
) {
}
