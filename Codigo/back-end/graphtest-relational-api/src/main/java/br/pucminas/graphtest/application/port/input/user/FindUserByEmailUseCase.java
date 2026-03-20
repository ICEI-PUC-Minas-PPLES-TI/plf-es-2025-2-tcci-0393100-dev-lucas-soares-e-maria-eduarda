package br.pucminas.graphtest.application.port.input.user;

import br.pucminas.graphtest.application.port.input.user.records.FindUserByEmailInput;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;

public interface FindUserByEmailUseCase {
    UserOutput execute(FindUserByEmailInput input);
}
