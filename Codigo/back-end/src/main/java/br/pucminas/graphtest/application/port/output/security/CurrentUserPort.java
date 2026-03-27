package br.pucminas.graphtest.application.port.output.security;

import br.pucminas.graphtest.application.domain.records.AuthenticatedUser;


public interface CurrentUserPort {
    AuthenticatedUser getCurrentUser();
}
