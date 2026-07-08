package br.pucminas.graphtest.application.usecases.security;

import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import br.pucminas.graphtest.application.domain.user.model.User;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;
import br.pucminas.graphtest.application.port.output.security.TokenServicePort;
import br.pucminas.graphtest.application.security.AuthenticatedUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserByTokenUseCaseImplTest {

    @Mock
    private TokenServicePort tokenService;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private AuthenticatedUserByTokenUseCaseImpl useCase;

    @Test
    void shouldReturnAuthenticatedUserWhenTokenAndUserAreValid() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "Usuario", "usuario@teste.com", "hash", UserProfileEnum.ADMIN);
        when(tokenService.tokenValido("token-valido")).thenReturn(true);
        when(tokenService.getEmailUsuario("token-valido")).thenReturn("usuario@teste.com");
        when(userRepositoryPort.findByEmail("usuario@teste.com")).thenReturn(Optional.of(user));

        AuthenticatedUser result = useCase.execute("token-valido");

        assertEquals(userId, result.id());
        assertEquals("usuario@teste.com", result.username());
        assertEquals(UserProfileEnum.ADMIN, result.profile());
    }

    @Test
    void shouldReturnNullWhenTokenIsInvalid() {
        when(tokenService.tokenValido("token-invalido")).thenReturn(false);

        assertNull(useCase.execute("token-invalido"));
        verify(tokenService, never()).getEmailUsuario(org.mockito.ArgumentMatchers.anyString());
        verify(userRepositoryPort, never()).findByEmail(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void shouldReturnNullWhenTokenHasNoEmailClaim() {
        when(tokenService.tokenValido("token-sem-email")).thenReturn(true);
        when(tokenService.getEmailUsuario("token-sem-email")).thenReturn(null);

        assertNull(useCase.execute("token-sem-email"));
        verify(userRepositoryPort, never()).findByEmail(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void shouldReturnNullWhenUserNoLongerExists() {
        when(tokenService.tokenValido("token-valido")).thenReturn(true);
        when(tokenService.getEmailUsuario("token-valido")).thenReturn("removido@teste.com");
        when(userRepositoryPort.findByEmail("removido@teste.com")).thenReturn(Optional.empty());

        assertNull(useCase.execute("token-valido"));
    }
}
