package br.pucminas.graphtest.application.port.input.user;

import br.pucminas.graphtest.application.domain.entity.User;

import java.util.List;

public interface ListUsersUseCase {
    List<User> execute();
}
