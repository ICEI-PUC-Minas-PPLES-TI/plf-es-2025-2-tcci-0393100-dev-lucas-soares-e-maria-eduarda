package br.pucminas.graphtest.adapters.inbound.dto;

import br.pucminas.graphtest.application.port.input.security.result.TokenValidationResult;
import lombok.Builder;

@Builder
public record TokenValidationDTO(
        boolean valid,
        String email
) {
    public static TokenValidationDTO from(TokenValidationResult result) {
        return new TokenValidationDTO(result.valid(), result.email());
    }
}
