package br.pucminas.graphtest.application.port.input.user;

import br.pucminas.graphtest.application.port.input.user.records.FindUserByIdInput;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;

public interface FindUserByIdUseCasePort {
    UserOutput execute(FindUserByIdInput input);
}
