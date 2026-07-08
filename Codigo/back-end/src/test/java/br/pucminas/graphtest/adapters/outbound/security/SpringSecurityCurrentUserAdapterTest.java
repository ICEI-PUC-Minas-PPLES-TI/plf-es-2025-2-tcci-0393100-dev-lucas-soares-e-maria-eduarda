package br.pucminas.graphtest.adapters.outbound.security;

import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import br.pucminas.graphtest.application.security.AuthenticatedUser;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class SpringSecurityCurrentUserAdapterTest {

    private final SpringSecurityCurrentUserAdapter adapter = new SpringSecurityCurrentUserAdapter();

    @Test
    void shouldReturnAuthenticatedUserFromSecurityContext() {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(UUID.randomUUID(), "usuario@teste.com", UserProfileEnum.USUARIO);

        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            holder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(authenticatedUser);

            AuthenticatedUser result = adapter.getCurrentUser();

            assertSame(authenticatedUser, result);
        }
    }

    @Test
    void shouldThrowWhenPrincipalIsNotAuthenticatedUser() {
        try (MockedStatic<SecurityContextHolder> holder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            holder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn("anonymousUser");

            assertThrows(IllegalStateException.class, adapter::getCurrentUser);
        }
    }
}
