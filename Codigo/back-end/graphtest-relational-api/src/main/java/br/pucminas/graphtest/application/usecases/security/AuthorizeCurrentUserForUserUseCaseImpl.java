package br.pucminas.graphtest.application.usecases.security;

import br.pucminas.graphtest.application.domain.model.AuthenticatedUser;
import br.pucminas.graphtest.application.exception.UnauthorizedUserException;
import br.pucminas.graphtest.application.port.input.security.AuthorizeCurrentUserForUserUseCase;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.lang.String.format;

@Service
public class AuthorizeCurrentUserForUserUseCaseImpl implements AuthorizeCurrentUserForUserUseCase {

    private final CurrentUserPort currentUserPort;

    public AuthorizeCurrentUserForUserUseCaseImpl(CurrentUserPort currentUserPort) {
        this.currentUserPort = currentUserPort;
    }

    @Override
    public AuthenticatedUser execute(UUID userId) {
        AuthenticatedUser currentUser = currentUserPort.getCurrentUser();

        if (!currentUser.isAdmin() && !currentUser.id().equals(userId)) {
            throw new UnauthorizedUserException(
                    format("usuario [%s] nao possui autorizacao para utilizar esse metodo", currentUser.username())
            );
        }

        return currentUser;
    }
}
