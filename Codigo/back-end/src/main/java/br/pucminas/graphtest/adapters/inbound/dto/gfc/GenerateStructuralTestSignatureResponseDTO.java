package br.pucminas.graphtest.adapters.inbound.dto.gfc;

import java.util.List;
import java.util.UUID;

public record GenerateStructuralTestSignatureResponseDTO(
        UUID gfcId,
        String methodSignature,
        int cyclomaticComplexity,
        List<StructuralTestMethodSignatureDTO> testMethods,
        String generatedCode
) {
}
