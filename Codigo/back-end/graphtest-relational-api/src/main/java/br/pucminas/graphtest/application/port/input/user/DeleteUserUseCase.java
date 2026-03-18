package br.pucminas.graphtest.application.port.input.user;

import br.pucminas.graphtest.application.port.input.user.command.DeleteUserCommand;

public interface DeleteUserUseCase {
    void execute(DeleteUserCommand command);
}
