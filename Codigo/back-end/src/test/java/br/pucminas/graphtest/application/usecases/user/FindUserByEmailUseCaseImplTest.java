package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import br.pucminas.graphtest.application.domain.user.model.User;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.user.records.FindUserByEmailInput;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;
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
class FindUserByEmailUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private FindUserByEmailUseCaseImpl useCase;

    @Test
    void shouldReturnUserWhenEmailExists() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "Usuario", "usuario@teste.com", "hash", UserProfileEnum.USUARIO);
        when(userRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(user));

        UserOutput output = useCase.execute(new FindUserByEmailInput("usuario@teste.com"));

        assertEquals(userId, output.id());
        assertEquals("usuario@teste.com", output.email());
    }

    @Test
    void shouldThrowNotFoundWhenEmailDoesNotExist() {
        when(userRepository.findByEmail("ausente@teste.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(new FindUserByEmailInput("ausente@teste.com")));
    }
}
