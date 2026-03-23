package br.pucminas.graphtest.application.port.input.user;

import br.pucminas.graphtest.application.port.input.user.records.FindUserByIdInput;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;

public interface FindUserByIdUseCase {
    UserOutput execute(FindUserByIdInput input);
}
