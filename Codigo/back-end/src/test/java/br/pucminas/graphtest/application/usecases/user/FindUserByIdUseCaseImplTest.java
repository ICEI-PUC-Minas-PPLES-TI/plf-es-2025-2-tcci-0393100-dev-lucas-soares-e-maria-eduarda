package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import br.pucminas.graphtest.application.domain.user.model.User;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.exception.UnauthorizedUserException;
import br.pucminas.graphtest.application.port.input.user.records.FindUserByIdInput;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;
import br.pucminas.graphtest.application.service.user.interfaces.UserAuthorizationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindUserByIdUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private UserAuthorizationService userAuthorizationService;

    @InjectMocks
    private FindUserByIdUseCaseImpl useCase;

    @Test
    void shouldAuthorizeAndReturnUser() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "Usuario", "usuario@teste.com", "hash", UserProfileEnum.USUARIO);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserOutput output = useCase.execute(new FindUserByIdInput(userId));

        assertEquals(userId, output.id());
        verify(userAuthorizationService).authorizeForUser(userId);
    }

    @Test
    void shouldThrowNotFoundWhenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> useCase.execute(new FindUserByIdInput(userId)));
    }

    @Test
    void shouldPropagateAuthorizationFailure() {
        UUID userId = UUID.randomUUID();
        doThrow(new UnauthorizedUserException("Sem permissao")).when(userAuthorizationService).authorizeForUser(userId);

        assertThrows(UnauthorizedUserException.class, () -> useCase.execute(new FindUserByIdInput(userId)));
    }
}
