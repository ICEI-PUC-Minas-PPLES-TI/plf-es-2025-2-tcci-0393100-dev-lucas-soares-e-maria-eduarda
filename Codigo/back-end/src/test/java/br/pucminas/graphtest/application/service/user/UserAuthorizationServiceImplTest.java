package br.pucminas.graphtest.application.service.user;

import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import br.pucminas.graphtest.application.exception.UnauthorizedUserException;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;
import br.pucminas.graphtest.application.security.AuthenticatedUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAuthorizationServiceImplTest {

    @Mock
    private CurrentUserPort currentUserPort;

    @InjectMocks
    private UserAuthorizationServiceImpl service;

    @Test
    void shouldAuthorizeAdminUser() {
        AuthenticatedUser admin = new AuthenticatedUser(UUID.randomUUID(), "admin", UserProfileEnum.ADMIN);
        when(currentUserPort.getCurrentUser()).thenReturn(admin);

        assertSame(admin, service.authorizeAdmin());
    }

    @Test
    void shouldThrowWhenNonAdminTriesToAuthorizeAdmin() {
        AuthenticatedUser regular = new AuthenticatedUser(UUID.randomUUID(), "usuario", UserProfileEnum.USUARIO);
        when(currentUserPort.getCurrentUser()).thenReturn(regular);

        assertThrows(UnauthorizedUserException.class, () -> service.authorizeAdmin());
    }

    @Test
    void shouldAuthorizeAdminForAnyUser() {
        UUID targetUserId = UUID.randomUUID();
        AuthenticatedUser admin = new AuthenticatedUser(UUID.randomUUID(), "admin", UserProfileEnum.ADMIN);
        when(currentUserPort.getCurrentUser()).thenReturn(admin);

        assertSame(admin, service.authorizeForUser(targetUserId));
    }

    @Test
    void shouldAuthorizeUserForOwnId() {
        UUID userId = UUID.randomUUID();
        AuthenticatedUser user = new AuthenticatedUser(userId, "usuario", UserProfileEnum.USUARIO);
        when(currentUserPort.getCurrentUser()).thenReturn(user);

        assertSame(user, service.authorizeForUser(userId));
    }

    @Test
    void shouldThrowWhenNonAdminTriesToAuthorizeForAnotherUser() {
        AuthenticatedUser user = new AuthenticatedUser(UUID.randomUUID(), "usuario", UserProfileEnum.USUARIO);
        when(currentUserPort.getCurrentUser()).thenReturn(user);

        assertThrows(UnauthorizedUserException.class, () -> service.authorizeForUser(UUID.randomUUID()));
    }
}
