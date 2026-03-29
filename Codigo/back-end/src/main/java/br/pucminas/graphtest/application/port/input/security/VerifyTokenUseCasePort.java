package br.pucminas.graphtest.application.port.input.security;

import br.pucminas.graphtest.application.port.input.security.records.TokenValidationResult;

public interface VerifyTokenUseCasePort {

    TokenValidationResult execute(String token);
}
