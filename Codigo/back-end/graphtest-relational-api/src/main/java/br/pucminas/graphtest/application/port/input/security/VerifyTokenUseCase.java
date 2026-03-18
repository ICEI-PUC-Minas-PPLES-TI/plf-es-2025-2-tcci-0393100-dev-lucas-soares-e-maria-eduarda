package br.pucminas.graphtest.application.port.input.security;

import br.pucminas.graphtest.application.port.input.security.result.TokenValidationResult;

public interface VerifyTokenUseCase {

    TokenValidationResult execute(String token);
}
