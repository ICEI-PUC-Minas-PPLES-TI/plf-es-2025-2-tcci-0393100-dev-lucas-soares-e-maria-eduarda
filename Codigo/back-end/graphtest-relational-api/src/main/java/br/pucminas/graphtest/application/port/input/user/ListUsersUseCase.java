package br.pucminas.graphtest.application.port.input.user;

import br.pucminas.graphtest.application.port.input.user.result.UserResult;

import java.util.List;

public interface ListUsersUseCase {
    List<UserResult> execute();
}
