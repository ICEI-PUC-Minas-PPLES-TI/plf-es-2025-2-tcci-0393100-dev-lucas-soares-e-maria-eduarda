package br.pucminas.graphtest.application.usecases.security;

import br.pucminas.graphtest.application.domain.records.AuthenticatedUser;
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

    // Pra que ser o método da linha 24? R: Para verificar se o usuário atual tem autorização para acessar os dados do usuário solicitado. Se o usuário atual for um administrador ou se for o próprio usuário, ele tem autorização. Caso contrário, uma exceção de usuário não autorizado é lançada.
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
