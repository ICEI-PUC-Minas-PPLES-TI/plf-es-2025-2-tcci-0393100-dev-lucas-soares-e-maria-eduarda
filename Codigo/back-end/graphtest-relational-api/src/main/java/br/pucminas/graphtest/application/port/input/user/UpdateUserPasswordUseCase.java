package br.pucminas.graphtest.application.port.input.user;

import br.pucminas.graphtest.application.port.input.user.command.UpdateUserPasswordCommand;

public interface UpdateUserPasswordUseCase {
    void execute(UpdateUserPasswordCommand command);
}
