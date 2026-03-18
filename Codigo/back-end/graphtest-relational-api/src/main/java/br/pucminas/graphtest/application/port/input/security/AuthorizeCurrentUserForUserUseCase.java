package br.pucminas.graphtest.application.port.input.security;

import br.pucminas.graphtest.application.domain.model.AuthenticatedUser;

import java.util.UUID;

public interface AuthorizeCurrentUserForUserUseCase {
    AuthenticatedUser execute(UUID userId);
}
