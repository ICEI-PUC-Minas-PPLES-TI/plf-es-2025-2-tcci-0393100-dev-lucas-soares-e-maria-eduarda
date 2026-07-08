package br.pucminas.graphtest.application.usecases.security;

import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import br.pucminas.graphtest.application.domain.user.model.User;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.security.records.AuthenticationUserResult;
import br.pucminas.graphtest.application.port.input.security.records.LoadAuthenticationUserInput;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoadAuthenticationUserUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private LoadAuthenticationUserUseCaseImpl useCase;

    @Test
    void shouldReturnAuthenticationDataWhenUserExists() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "Usuario", "usuario@teste.com", "hash", UserProfileEnum.USUARIO);
        when(userRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(user));

        AuthenticationUserResult result = useCase.execute(new LoadAuthenticationUserInput("usuario@teste.com"));

        assertEquals(userId, result.id());
        assertEquals("usuario@teste.com", result.email());
        assertEquals("hash", result.password());
        assertEquals(UserProfileEnum.USUARIO, result.profile());
    }

    @Test
    void shouldThrowNotFoundWhenUserDoesNotExist() {
        when(userRepository.findByEmail("ausente@teste.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(new LoadAuthenticationUserInput("ausente@teste.com")));
    }
}
