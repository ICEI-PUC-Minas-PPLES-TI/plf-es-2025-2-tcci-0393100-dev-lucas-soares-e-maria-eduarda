package br.pucminas.graphtest.adapters.inbound.dto.gfc;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Request para criacao persistida de um Grafo de Fluxo de Controle.
 */
public record CreateGfcDTO(
        @NotNull
        UUID projectId,

        @NotNull
        UUID sourceFileId,

        @NotBlank
        String methodSignature,

        @NotBlank
        String name,

        String description
) {
}
