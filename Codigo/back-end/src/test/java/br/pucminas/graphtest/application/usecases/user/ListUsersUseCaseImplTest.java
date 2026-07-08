package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import br.pucminas.graphtest.application.domain.user.model.User;
import br.pucminas.graphtest.application.exception.UnauthorizedUserException;
import br.pucminas.graphtest.application.port.input.user.records.UserOutput;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;
import br.pucminas.graphtest.application.service.user.interfaces.UserAuthorizationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListUsersUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private UserAuthorizationService userAuthorizationService;

    @InjectMocks
    private ListUsersUseCaseImpl useCase;

    @Test
    void shouldAuthorizeAdminAndListAllUsers() {
        User user = new User(UUID.randomUUID(), "Usuario", "usuario@teste.com", "hash", UserProfileEnum.USUARIO);
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserOutput> outputs = useCase.execute();

        assertEquals(1, outputs.size());
        InOrder inOrder = inOrder(userAuthorizationService, userRepository);
        inOrder.verify(userAuthorizationService).authorizeAdmin();
        inOrder.verify(userRepository).findAll();
    }

    @Test
    void shouldPropagateAuthorizationFailureWithoutListingUsers() {
        doThrow(new UnauthorizedUserException("Sem permissao")).when(userAuthorizationService).authorizeAdmin();

        assertThrows(UnauthorizedUserException.class, useCase::execute);
    }
}
