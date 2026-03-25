package br.pucminas.graphtest.application.service;

import br.pucminas.graphtest.application.domain.records.AuthenticatedUser;
import br.pucminas.graphtest.application.exception.UnauthorizedUserException;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;
import br.pucminas.graphtest.application.service.interfaces.UserAuthorizationService;

import java.util.UUID;

import static java.lang.String.format;

public class UserAuthorizationServiceImpl implements UserAuthorizationService {

    private final CurrentUserPort currentUserPort;

    public UserAuthorizationServiceImpl(CurrentUserPort currentUserPort) {
        this.currentUserPort = currentUserPort;
    }

    @Override
    public AuthenticatedUser authorizeAdmin() {
        AuthenticatedUser currentUser = currentUserPort.getCurrentUser();

        if (!currentUser.isAdmin()) {
            throw new UnauthorizedUserException(
                    format("usuario [%s] nao possui autorizacao para utilizar esse metodo", currentUser.username())
            );
        }

        return currentUser;
    }

    @Override
    public AuthenticatedUser authorizeForUser(UUID userId) {
        AuthenticatedUser currentUser = currentUserPort.getCurrentUser();

        if (!currentUser.isAdmin() && !currentUser.id().equals(userId)) {
            throw new UnauthorizedUserException(
                    format("usuario [%s] nao possui autorizacao para utilizar esse metodo", currentUser.username())
            );
        }

        return currentUser;
    }
}
