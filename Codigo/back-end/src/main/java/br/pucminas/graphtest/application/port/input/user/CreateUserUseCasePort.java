package br.pucminas.graphtest.application.port.input.user;

import br.pucminas.graphtest.application.port.input.user.records.CreateUserInput;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;

public interface CreateUserUseCasePort {
    UserOutput execute(CreateUserInput input);
}
