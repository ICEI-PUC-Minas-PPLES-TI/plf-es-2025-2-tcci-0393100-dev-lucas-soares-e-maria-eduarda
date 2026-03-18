package br.pucminas.graphtest.application.port.input.user;

import br.pucminas.graphtest.application.port.input.user.query.FindUserByEmailQuery;
import br.pucminas.graphtest.application.port.input.user.result.UserResult;

public interface FindUserByEmailUseCase {
    UserResult execute(FindUserByEmailQuery query);
}
