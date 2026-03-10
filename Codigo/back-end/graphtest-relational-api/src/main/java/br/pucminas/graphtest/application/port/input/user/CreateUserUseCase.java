package br.pucminas.graphtest.application.port.input.user;

import br.pucminas.graphtest.application.domain.entity.User;
import br.pucminas.graphtest.application.port.input.user.command.CreateUserCommand;

public interface CreateUserUseCase {
    User execute(CreateUserCommand command);
}
