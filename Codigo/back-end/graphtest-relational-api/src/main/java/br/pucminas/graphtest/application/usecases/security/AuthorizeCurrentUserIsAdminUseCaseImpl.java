package br.pucminas.graphtest.application.usecases.security;

import br.pucminas.graphtest.application.domain.AuthenticatedUser;
import br.pucminas.graphtest.application.exception.UnauthorizedUserException;
import br.pucminas.graphtest.application.port.input.security.AuthorizeCurrentUserIsAdminUseCase;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class AuthorizeCurrentUserIsAdminUseCaseImpl implements AuthorizeCurrentUserIsAdminUseCase {

    private final CurrentUserPort currentUserPort;

    public AuthorizeCurrentUserIsAdminUseCaseImpl(CurrentUserPort currentUserPort) {
        this.currentUserPort = currentUserPort;
    }

    @Override
    public AuthenticatedUser execute() {
        AuthenticatedUser currentUser = currentUserPort.getCurrentUser();

        if (!currentUser.isAdmin()) {
            throw new UnauthorizedUserException(
                    format("usuario [%s] nao possui autorizacao para utilizar esse metodo", currentUser.username())
            );
        }

        return currentUser;
    }
}
