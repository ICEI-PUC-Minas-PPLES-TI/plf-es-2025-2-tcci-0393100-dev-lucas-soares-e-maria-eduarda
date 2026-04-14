package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.user.model.User;
import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import br.pucminas.graphtest.application.exception.DuplicateEmailException;
import br.pucminas.graphtest.application.port.input.user.records.CreateUserInput;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;
import br.pucminas.graphtest.application.port.output.security.PasswordEncoderPort;
import br.pucminas.graphtest.application.service.user.interfaces.UserEmailUniquenessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private UserEmailUniquenessService userEmailUniquenessService;

    @InjectMocks
    private CreateUserUseCaseImpl useCase;

    @Test
    void shouldRejectDuplicateEmailBeforePersisting() {
        CreateUserInput input = new CreateUserInput("Usuario Teste", "duplicado@teste.com", "senha123");
        org.mockito.Mockito.doThrow(new DuplicateEmailException("Ja existe um usuario cadastrado com o email informado"))
                .when(userEmailUniquenessService).ensureEmailAvailable(input.email());

        assertThrows(DuplicateEmailException.class, () -> useCase.execute(input));

        verify(userEmailUniquenessService).ensureEmailAvailable(input.email());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldCreateUserWhenEmailIsAvailable() {
        CreateUserInput input = new CreateUserInput("Usuario Teste", "usuario@teste.com", "senha123");
        UUID userId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        when(passwordEncoder.encode(input.password())).thenReturn("senha-criptografada");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            User persistedUser = new User(userId, input.name(), input.email(), "senha-criptografada", UserProfileEnum.USUARIO);
            persistedUser.setCreatedAt(createdAt);
            persistedUser.setUpdatedAt(createdAt);
            return persistedUser;
        });

        UserOutput output = useCase.execute(input);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        assertEquals(userId, output.id());
        assertEquals(input.email(), output.email());
        verify(userEmailUniquenessService).ensureEmailAvailable(input.email());
        verify(userRepository).save(userCaptor.capture());
        assertNotNull(userCaptor.getValue().getCreatedAt());
        assertNull(userCaptor.getValue().getUpdatedAt());
        assertNull(output.updatedAt());
    }
}
