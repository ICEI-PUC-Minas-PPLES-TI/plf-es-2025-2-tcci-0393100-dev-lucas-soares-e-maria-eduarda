package br.pucminas.graphtest.application.port.input.user;

import br.pucminas.graphtest.application.port.input.user.records.DeleteUserInput;

public interface DeleteUserUseCase {
    void execute(DeleteUserInput input);
}
