package br.pucminas.graphtest.application.usecases.security;

import br.pucminas.graphtest.application.port.input.security.GenerateTokenUseCase;
import br.pucminas.graphtest.application.port.input.security.command.GenerateTokenCommand;
import br.pucminas.graphtest.application.port.output.security.TokenServicePort;
import org.springframework.stereotype.Service;

@Service
public class GenerateTokenUseCaseImpl implements GenerateTokenUseCase {

    private final TokenServicePort tokenService;

    public GenerateTokenUseCaseImpl(TokenServicePort tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public String execute(GenerateTokenCommand command) {
        return tokenService.gerarToken(
                command.email(),
                command.profile().name().toLowerCase(),
                command.userId()
        );
    }
}
