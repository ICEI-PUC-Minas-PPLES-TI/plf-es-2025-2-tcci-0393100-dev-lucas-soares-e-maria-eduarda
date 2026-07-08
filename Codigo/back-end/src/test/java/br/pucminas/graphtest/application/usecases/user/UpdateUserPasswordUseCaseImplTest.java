package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import br.pucminas.graphtest.application.domain.user.model.User;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.exception.UnauthorizedUserException;
import br.pucminas.graphtest.application.exception.UpdatePasswordException;
import br.pucminas.graphtest.application.port.input.user.records.UpdateUserPasswordInput;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;
import br.pucminas.graphtest.application.port.output.security.PasswordEncoderPort;
import br.pucminas.graphtest.application.service.user.interfaces.UserAuthorizationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateUserPasswordUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private UserAuthorizationService userAuthorizationService;

    @InjectMocks
    private UpdateUserPasswordUseCaseImpl useCase;

    @Test
    void shouldUpdatePasswordWhenOriginalPasswordMatches() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "Usuario", "usuario@teste.com", "hash-antigo", UserProfileEnum.USUARIO);
        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senha-antiga", "hash-antigo")).thenReturn(true);
        when(passwordEncoder.encode("senha-nova")).thenReturn("hash-novo");

        useCase.execute(new UpdateUserPasswordInput(userId, "senha-antiga", "senha-nova"));

        assertEquals("hash-novo", user.getPassword());
        assertNotNull(user.getUpdatedAt());
        verify(userRepositoryPort).save(user);
    }

    @Test
    void shouldUpdatePasswordWithoutCheckingOriginalWhenNotProvided() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "Usuario", "usuario@teste.com", "hash-antigo", UserProfileEnum.USUARIO);
        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("senha-nova")).thenReturn("hash-novo");

        useCase.execute(new UpdateUserPasswordInput(userId, null, "senha-nova"));

        assertEquals("hash-novo", user.getPassword());
        verify(passwordEncoder, never()).matches(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
        verify(userRepositoryPort).save(user);
    }

    @Test
    void shouldThrowWhenOriginalPasswordDoesNotMatch() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "Usuario", "usuario@teste.com", "hash-antigo", UserProfileEnum.USUARIO);
        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senha-errada", "hash-antigo")).thenReturn(false);

        assertThrows(UpdatePasswordException.class,
                () -> useCase.execute(new UpdateUserPasswordInput(userId, "senha-errada", "senha-nova")));
        verify(userRepositoryPort, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldThrowNotFoundWhenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(userRepositoryPort.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(new UpdateUserPasswordInput(userId, null, "senha-nova")));
    }

    @Test
    void shouldPropagateAuthorizationFailure() {
        UUID userId = UUID.randomUUID();
        doThrow(new UnauthorizedUserException("Sem permissao")).when(userAuthorizationService).authorizeForUser(userId);

        assertThrows(UnauthorizedUserException.class,
                () -> useCase.execute(new UpdateUserPasswordInput(userId, null, "senha-nova")));
        verify(userRepositoryPort, never()).findById(org.mockito.ArgumentMatchers.any());
    }
}
