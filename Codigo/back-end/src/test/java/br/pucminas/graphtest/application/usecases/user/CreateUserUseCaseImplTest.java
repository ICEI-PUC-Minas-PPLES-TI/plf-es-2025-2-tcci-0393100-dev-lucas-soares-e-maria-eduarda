package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.User;
import br.pucminas.graphtest.application.domain.enums.UserProfileEnum;
import br.pucminas.graphtest.application.exception.DuplicateEmailException;
import br.pucminas.graphtest.application.port.input.user.records.CreateUserInput;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;
import br.pucminas.graphtest.application.port.output.security.PasswordEncoderPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @InjectMocks
    private CreateUserUseCaseImpl useCase;

    @Test
    void shouldRejectDuplicateEmailBeforePersisting() {
        CreateUserInput input = new CreateUserInput("Usuario Teste", "duplicado@teste.com", "senha123");
        when(userRepository.existsByEmail(input.email())).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> useCase.execute(input));

        verify(userRepository).existsByEmail(input.email());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldCreateUserWhenEmailIsAvailable() {
        CreateUserInput input = new CreateUserInput("Usuario Teste", "usuario@teste.com", "senha123");
        UUID userId = UUID.randomUUID();

        when(userRepository.existsByEmail(input.email())).thenReturn(false);
        when(passwordEncoder.encode(input.password())).thenReturn("senha-criptografada");
        when(userRepository.save(any(User.class))).thenReturn(
                new User(userId, input.name(), input.email(), "senha-criptografada", UserProfileEnum.USUARIO)
        );

        UserOutput output = useCase.execute(input);

        assertEquals(userId, output.id());
        assertEquals(input.email(), output.email());
        verify(userRepository).existsByEmail(input.email());
        verify(userRepository).save(any(User.class));
    }
}
