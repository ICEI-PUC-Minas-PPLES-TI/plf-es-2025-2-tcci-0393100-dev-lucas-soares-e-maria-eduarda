package br.pucminas.graphtest.adapters.inbound.security;

import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import br.pucminas.graphtest.application.port.input.security.LoadAuthenticationUserUseCasePort;
import br.pucminas.graphtest.application.port.input.security.records.AuthenticationUserResult;
import br.pucminas.graphtest.application.port.input.security.records.LoadAuthenticationUserInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsAdapterTest {

    @Mock
    private LoadAuthenticationUserUseCasePort loadAuthenticationUserUseCasePort;

    @InjectMocks
    private CustomUserDetailsAdapter adapter;

    @Test
    void shouldBuildUserDetailsFromAuthenticationResult() {
        UUID userId = UUID.randomUUID();
        AuthenticationUserResult result = new AuthenticationUserResult(userId, "usuario@teste.com", "hash", UserProfileEnum.USUARIO);
        when(loadAuthenticationUserUseCasePort.execute(new LoadAuthenticationUserInput("usuario@teste.com"))).thenReturn(result);

        UserDetails userDetails = adapter.loadUserByUsername("usuario@teste.com");

        assertEquals("usuario@teste.com", userDetails.getUsername());
        assertEquals("hash", userDetails.getPassword());
        assertEquals("ROLE_USUARIO", userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void shouldWrapFailureAsUsernameNotFoundException() {
        when(loadAuthenticationUserUseCasePort.execute(new LoadAuthenticationUserInput("ausente@teste.com")))
                .thenThrow(new RuntimeException("Usuario nao encontrado"));

        assertThrows(UsernameNotFoundException.class, () -> adapter.loadUserByUsername("ausente@teste.com"));
    }
}
