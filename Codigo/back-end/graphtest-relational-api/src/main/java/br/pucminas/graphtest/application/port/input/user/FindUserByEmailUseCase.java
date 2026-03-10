package br.pucminas.graphtest.application.port.input.user;

import br.pucminas.graphtest.application.domain.entity.User;

public interface FindUserByEmailUseCase {
    User execute(String email);
}
