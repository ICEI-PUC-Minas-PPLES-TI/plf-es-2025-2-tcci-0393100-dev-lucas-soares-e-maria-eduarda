package br.pucminas.graphtest.application.port.input.user;

import br.pucminas.graphtest.application.domain.entity.User;

import java.util.UUID;

public interface FindUserByIdUseCase {
    User execute(UUID id);
}
