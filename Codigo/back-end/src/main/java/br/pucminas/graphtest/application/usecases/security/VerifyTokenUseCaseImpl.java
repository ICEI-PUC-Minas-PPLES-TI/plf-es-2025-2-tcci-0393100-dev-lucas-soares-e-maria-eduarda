package br.pucminas.graphtest.application.usecases.security;

import br.pucminas.graphtest.application.port.input.security.VerifyTokenUseCasePort;
import br.pucminas.graphtest.application.port.input.security.records.TokenValidationResult;
import br.pucminas.graphtest.application.port.output.security.TokenServicePort;

public class VerifyTokenUseCaseImpl implements VerifyTokenUseCasePort {

    private final TokenServicePort tokenService;

    public VerifyTokenUseCaseImpl(TokenServicePort tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public TokenValidationResult execute(String token) {
        boolean valid = tokenService.tokenValido(token);
        String email = valid ? tokenService.getEmailUsuario(token) : null;
        return new TokenValidationResult(valid, email);
    }
}
