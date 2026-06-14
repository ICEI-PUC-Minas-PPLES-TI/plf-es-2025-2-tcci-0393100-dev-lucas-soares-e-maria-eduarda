package br.pucminas.graphtest.application.usecases.security;

import br.pucminas.graphtest.application.port.input.security.GenerateTokenUseCasePort;
import br.pucminas.graphtest.application.port.input.security.records.GenerateTokenInput;
import br.pucminas.graphtest.application.port.output.security.TokenServicePort;

public class GenerateTokenUseCaseImpl implements GenerateTokenUseCasePort {

    private final TokenServicePort tokenService;

    public GenerateTokenUseCaseImpl(TokenServicePort tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public String execute(GenerateTokenInput input) {
        return tokenService.gerarToken(
                input.email(),
                input.profile().name().toLowerCase(),
                input.userId()
        );
    }
}
