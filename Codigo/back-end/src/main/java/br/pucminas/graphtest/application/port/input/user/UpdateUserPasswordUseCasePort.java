package br.pucminas.graphtest.application.port.input.user;

import br.pucminas.graphtest.application.port.input.user.records.UpdateUserPasswordInput;

public interface UpdateUserPasswordUseCasePort {
    void execute(UpdateUserPasswordInput input);
}
