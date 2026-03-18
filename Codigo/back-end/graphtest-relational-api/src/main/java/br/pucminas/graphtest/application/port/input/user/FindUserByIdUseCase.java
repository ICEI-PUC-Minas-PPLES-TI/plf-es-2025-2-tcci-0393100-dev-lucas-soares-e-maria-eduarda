package br.pucminas.graphtest.application.port.input.user;

import br.pucminas.graphtest.application.port.input.user.query.FindUserByIdQuery;
import br.pucminas.graphtest.application.port.input.user.result.UserResult;

public interface FindUserByIdUseCase {
    UserResult execute(FindUserByIdQuery query);
}
