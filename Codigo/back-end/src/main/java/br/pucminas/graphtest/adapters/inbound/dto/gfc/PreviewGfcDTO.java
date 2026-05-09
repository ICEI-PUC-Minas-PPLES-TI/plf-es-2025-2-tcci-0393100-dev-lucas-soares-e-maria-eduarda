package br.pucminas.graphtest.adapters.inbound.dto.gfc;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

/**
 * Request para pre-visualizacao de um Grafo de Fluxo de Controle.
 */
@Builder
public record PreviewGfcDTO(
        @NotNull
        UUID projectId,

        @NotBlank
        String name,

        String description,

        @NotBlank
        String sourceCode,

        String methodSignature
) {
}
