package br.pucminas.graphtest.adapters.inbound.security;

import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import br.pucminas.graphtest.application.port.input.security.AuthenticatedUserByTokenUseCasePort;
import br.pucminas.graphtest.application.security.AuthenticatedUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthorizationFilterAdapterTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthenticatedUserByTokenUseCasePort authenticatedUserByTokenUseCasePort;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateAndContinueChainWhenTokenIsValid() throws Exception {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(UUID.randomUUID(), "usuario@teste.com", UserProfileEnum.USUARIO);
        when(request.getHeader("Authorization")).thenReturn("Bearer abc.def.ghi");
        when(authenticatedUserByTokenUseCasePort.execute("abc.def.ghi")).thenReturn(authenticatedUser);

        JwtAuthorizationFilterAdapter filter = new JwtAuthorizationFilterAdapter(authenticationManager, authenticatedUserByTokenUseCasePort);
        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldContinueChainWithoutAuthenticatingWhenTokenIsInvalid() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token-invalido");
        when(authenticatedUserByTokenUseCasePort.execute("token-invalido")).thenReturn(null);

        JwtAuthorizationFilterAdapter filter = new JwtAuthorizationFilterAdapter(authenticationManager, authenticatedUserByTokenUseCasePort);
        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldContinueChainWithoutAuthenticatingWhenHeaderIsMissing() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        JwtAuthorizationFilterAdapter filter = new JwtAuthorizationFilterAdapter(authenticationManager, authenticatedUserByTokenUseCasePort);
        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldContinueChainWithoutAuthenticatingWhenHeaderIsNotBearer() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic abc123");

        JwtAuthorizationFilterAdapter filter = new JwtAuthorizationFilterAdapter(authenticationManager, authenticatedUserByTokenUseCasePort);
        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}
