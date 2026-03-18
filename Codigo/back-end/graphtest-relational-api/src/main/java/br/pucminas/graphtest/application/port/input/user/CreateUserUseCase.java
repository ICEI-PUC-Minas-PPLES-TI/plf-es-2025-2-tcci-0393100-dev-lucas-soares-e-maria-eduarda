package br.pucminas.graphtest.application.port.input.user;

import br.pucminas.graphtest.application.port.input.user.command.CreateUserCommand;
import br.pucminas.graphtest.application.port.input.user.result.UserResult;

public interface CreateUserUseCase {
    UserResult execute(CreateUserCommand command);
}
