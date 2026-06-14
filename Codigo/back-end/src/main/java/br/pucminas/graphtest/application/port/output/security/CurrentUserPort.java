package br.pucminas.graphtest.application.port.output.security;

import br.pucminas.graphtest.application.security.AuthenticatedUser;


public interface CurrentUserPort {
    AuthenticatedUser getCurrentUser();
}
