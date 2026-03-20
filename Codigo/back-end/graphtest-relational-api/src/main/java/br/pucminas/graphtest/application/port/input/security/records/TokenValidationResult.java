package br.pucminas.graphtest.application.port.input.security.records;

public record TokenValidationResult(
        boolean valid,
        String email
) {
}
