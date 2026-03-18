package br.pucminas.graphtest.application.port.input.security;

import br.pucminas.graphtest.application.domain.model.AuthenticatedUser;

public interface AuthorizeCurrentUserIsAdminUseCase {
    AuthenticatedUser execute();
}
