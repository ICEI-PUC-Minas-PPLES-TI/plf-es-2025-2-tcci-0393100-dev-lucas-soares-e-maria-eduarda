package br.pucminas.graphtest.application.port.input.user;

import java.util.UUID;

public interface DeleteUserUseCase {
    void execute(UUID id);
}
