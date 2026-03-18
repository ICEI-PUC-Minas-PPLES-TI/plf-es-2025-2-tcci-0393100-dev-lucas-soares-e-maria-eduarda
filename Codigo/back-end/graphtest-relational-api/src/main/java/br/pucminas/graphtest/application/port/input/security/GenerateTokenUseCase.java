package br.pucminas.graphtest.application.port.input.security;

import br.pucminas.graphtest.application.port.input.security.command.GenerateTokenCommand;

public interface GenerateTokenUseCase {

    String execute(GenerateTokenCommand command);
}
