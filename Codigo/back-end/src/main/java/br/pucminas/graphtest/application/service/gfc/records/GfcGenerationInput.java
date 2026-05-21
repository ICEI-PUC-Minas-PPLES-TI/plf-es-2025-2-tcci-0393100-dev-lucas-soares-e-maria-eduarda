package br.pucminas.graphtest.application.service.gfc.records;

import java.util.UUID;

/**
 * Entrada para geracao de um GFC a partir de codigo-fonte Java.
 */
public record GfcGenerationInput(
        UUID projectId,
        UUID sourceFileId,
        String sourceCode,
        String methodSignature,
        String name,
        String description
) {
}
