package br.pucminas.graphtest.adapters.inbound.security;

import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import br.pucminas.graphtest.application.port.input.security.GenerateTokenUseCasePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterAdapterTest {

    private AuthenticationManager authenticationManager;
    private GenerateTokenUseCasePort generateTokenUseCasePort;
    private JwtAuthenticationFilterAdapter filter;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        generateTokenUseCasePort = mock(GenerateTokenUseCasePort.class);
        filter = new JwtAuthenticationFilterAdapter(authenticationManager, generateTokenUseCasePort);
    }

    @Test
    void shouldAuthenticateUsingCredentialsFromRequestBody() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent("{\"email\":\"usuario@teste.com\",\"password\":\"senha123\"}".getBytes(StandardCharsets.UTF_8));
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication expectedAuthentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(expectedAuthentication);

        Authentication result = filter.attemptAuthentication(request, response);

        assertEquals(expectedAuthentication, result);
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("usuario@teste.com", "senha123", List.of()));
    }

    @Test
    void shouldThrowRuntimeExceptionWhenRequestBodyCannotBeParsed() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent(new byte[0]);
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThrows(RuntimeException.class, () -> filter.attemptAuthentication(request, response));
    }

    @Test
    void shouldWriteAuthorizationHeaderAndBodyOnSuccessfulAuthentication() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDetailsAdapter principal = UserDetailsAdapter.builder()
                .id(userId)
                .email("usuario@teste.com")
                .senha("hash")
                .perfilUsuario(UserProfileEnum.USUARIO)
                .build();
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(generateTokenUseCasePort.execute(any())).thenReturn("token-gerado");

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.successfulAuthentication(request, response, null, authentication);

        assertEquals("Bearer token-gerado", response.getHeader("Authorization"));
        assertTrue(response.getContentType().startsWith("application/json"));
        assertTrue(response.getContentAsString().contains("token-gerado"));
    }

    @Test
    void shouldThrowWhenPrincipalIsNotUserDetailsAdapter() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("nao-e-user-details");
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThrows(IllegalStateException.class,
                () -> filter.successfulAuthentication(request, response, null, authentication));
    }
}
