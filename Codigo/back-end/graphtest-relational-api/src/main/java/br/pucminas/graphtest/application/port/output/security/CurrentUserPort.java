package br.pucminas.graphtest.application.port.output.security;

import br.pucminas.graphtest.application.domain.user.AuthenticatedUser;


public interface CurrentUserPort {
    AuthenticatedUser getCurrentUser();
}
