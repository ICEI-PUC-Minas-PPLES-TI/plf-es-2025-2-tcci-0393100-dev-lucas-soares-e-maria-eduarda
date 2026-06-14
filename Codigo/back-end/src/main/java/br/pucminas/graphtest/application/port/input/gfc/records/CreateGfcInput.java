package br.pucminas.graphtest.application.port.input.gfc.records;

import java.util.UUID;

/**
 * Entrada para criacao persistida de um GFC.
 */
public record CreateGfcInput(
        UUID projectId,
        UUID sourceFileId,
        String methodSignature,
        String name,
        String description
) {
}
