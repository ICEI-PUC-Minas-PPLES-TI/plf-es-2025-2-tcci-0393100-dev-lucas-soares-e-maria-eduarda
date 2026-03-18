package br.pucminas.graphtest.application.port.input.security.result;

public record TokenValidationResult(
        boolean valid,
        String email
) {
}
