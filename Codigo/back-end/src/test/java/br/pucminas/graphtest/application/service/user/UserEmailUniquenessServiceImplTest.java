package br.pucminas.graphtest.application.service.user;

import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import br.pucminas.graphtest.application.domain.user.model.User;
import br.pucminas.graphtest.application.exception.DuplicateEmailException;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserEmailUniquenessServiceImplTest {

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private UserEmailUniquenessServiceImpl service;

    @Test
    void shouldNotThrowWhenEmailIsAvailable() {
        when(userRepository.existsByEmail("novo@teste.com")).thenReturn(false);

        assertDoesNotThrow(() -> service.ensureEmailAvailable("novo@teste.com"));
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("existente@teste.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> service.ensureEmailAvailable("existente@teste.com"));
    }

    @Test
    void shouldNotThrowForUpdateWhenEmailBelongsToSameUser() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "Usuario", "usuario@teste.com", "hash", UserProfileEnum.USUARIO);
        when(userRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> service.ensureEmailAvailableForUpdate(userId, "usuario@teste.com"));
    }

    @Test
    void shouldNotThrowForUpdateWhenEmailIsNotUsedByAnyone() {
        when(userRepository.findByEmail("novo@teste.com")).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> service.ensureEmailAvailableForUpdate(UUID.randomUUID(), "novo@teste.com"));
    }

    @Test
    void shouldThrowForUpdateWhenEmailBelongsToAnotherUser() {
        User otherUser = new User(UUID.randomUUID(), "Outro", "outro@teste.com", "hash", UserProfileEnum.USUARIO);
        when(userRepository.findByEmail("outro@teste.com")).thenReturn(Optional.of(otherUser));

        assertThrows(DuplicateEmailException.class,
                () -> service.ensureEmailAvailableForUpdate(UUID.randomUUID(), "outro@teste.com"));
    }
}
