package br.pucminas.graphtest.application.port.input.user;

import br.pucminas.graphtest.application.port.input.user.command.UpdateUserCommand;
import br.pucminas.graphtest.application.port.input.user.result.UserResult;

public interface UpdateUserUseCase {
    UserResult execute(UpdateUserCommand command);
}
