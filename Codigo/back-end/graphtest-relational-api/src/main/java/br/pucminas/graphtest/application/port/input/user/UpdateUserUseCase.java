package br.pucminas.graphtest.application.port.input.user;

import br.pucminas.graphtest.application.port.input.user.records.UpdateUserInput;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;

public interface UpdateUserUseCase {
    UserOutput execute(UpdateUserInput input);
}
