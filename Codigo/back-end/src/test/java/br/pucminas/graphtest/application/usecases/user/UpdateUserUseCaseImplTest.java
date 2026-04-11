package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.user.model.User;
import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import br.pucminas.graphtest.application.security.AuthenticatedUser;
import br.pucminas.graphtest.application.exception.DuplicateEmailException;
import br.pucminas.graphtest.application.exception.InvalidUserProfileException;
import br.pucminas.graphtest.application.port.input.user.records.UpdateUserInput;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;
import br.pucminas.graphtest.application.service.user.interfaces.UserAuthorizationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateUserUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private UserAuthorizationService userAuthorizationService;

    @InjectMocks
    private UpdateUserUseCaseImpl useCase;

    @Test
    void shouldRejectInvalidProfileCodeForAdmin() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "Usuario Antigo", "antigo@teste.com", "senha", UserProfileEnum.USUARIO);
        UpdateUserInput input = new UpdateUserInput(userId, "Usuario Novo", "novo@teste.com", 99);

        when(userAuthorizationService.authorizeForUser(userId))
                .thenReturn(new AuthenticatedUser(UUID.randomUUID(), "admin@teste.com", UserProfileEnum.ADMIN));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(InvalidUserProfileException.class, () -> useCase.execute(input));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldKeepCurrentProfileWhenAdminDoesNotSendProfileCode() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "Usuario Antigo", "antigo@teste.com", "senha", UserProfileEnum.USUARIO);
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        user.restoreAuditFields(createdAt, createdAt);
        UpdateUserInput input = new UpdateUserInput(userId, "Usuario Novo", "novo@teste.com", null);

        when(userAuthorizationService.authorizeForUser(userId))
                .thenReturn(new AuthenticatedUser(UUID.randomUUID(), "admin@teste.com", UserProfileEnum.ADMIN));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(input);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        assertEquals(UserProfileEnum.USUARIO, user.getProfile());
        verify(userRepository).save(userCaptor.capture());
        assertEquals(createdAt, userCaptor.getValue().getCreatedAt());
        assertNotNull(userCaptor.getValue().getUpdatedAt());
        assertTrue(!userCaptor.getValue().getUpdatedAt().isBefore(createdAt));
    }

    @Test
    void shouldRejectDuplicateEmailFromAnotherUser() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        User currentUser = new User(userId, "Usuario Atual", "atual@teste.com", "senha", UserProfileEnum.USUARIO);
        User otherUser = new User(otherUserId, "Outro Usuario", "duplicado@teste.com", "senha", UserProfileEnum.USUARIO);
        UpdateUserInput input = new UpdateUserInput(userId, "Usuario Novo", "duplicado@teste.com", null);

        when(userAuthorizationService.authorizeForUser(userId))
                .thenReturn(new AuthenticatedUser(userId, "usuario@teste.com", UserProfileEnum.USUARIO));
        when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));
        when(userRepository.findByEmail(input.email())).thenReturn(Optional.of(otherUser));

        assertThrows(DuplicateEmailException.class, () -> useCase.execute(input));

        verify(userRepository, never()).save(any(User.class));
    }
}
