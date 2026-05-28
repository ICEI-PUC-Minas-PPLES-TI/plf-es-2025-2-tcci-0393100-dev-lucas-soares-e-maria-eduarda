package br.pucminas.graphtest.application.port.input.gfc.records;

import java.util.List;
import java.util.UUID;

public record GenerateStructuralTestSignatureOutput(
        UUID gfcId,
        String methodSignature,
        int cyclomaticComplexity,
        List<StructuralTestMethodSignatureOutput> testMethods,
        String generatedCode
) {
}
