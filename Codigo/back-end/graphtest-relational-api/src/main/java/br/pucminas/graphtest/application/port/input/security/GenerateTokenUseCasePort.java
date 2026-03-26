package br.pucminas.graphtest.application.port.input.security;

import br.pucminas.graphtest.application.port.input.security.records.GenerateTokenInput;

public interface GenerateTokenUseCasePort {

    String execute(GenerateTokenInput input);
}
