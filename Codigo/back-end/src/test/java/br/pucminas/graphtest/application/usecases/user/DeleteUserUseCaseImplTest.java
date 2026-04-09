package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import br.pucminas.graphtest.application.domain.user.model.User;
import br.pucminas.graphtest.application.port.input.user.records.DeleteUserInput;
import br.pucminas.graphtest.application.port.output.repositories.UserRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectDeletionService;
import br.pucminas.graphtest.application.service.user.interfaces.UserAuthorizationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteUserUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private UserAuthorizationService userAuthorizationService;

    @Mock
    private ProjectDeletionService projectDeletionService;

    @InjectMocks
    private DeleteUserUseCaseImpl useCase;

    @Test
    void shouldDeleteUserProjectsThroughCentralizedDeletionServiceBeforeDeletingUser() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "Usuario de Teste", "teste@teste.com", "senha", UserProfileEnum.USUARIO);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        useCase.execute(new DeleteUserInput(userId));

        verify(userAuthorizationService).authorizeForUser(userId);
        verify(userRepository).findById(userId);
        verify(projectDeletionService).deleteProjectsByUserId(userId);
        verify(userRepository).deleteById(userId);
    }
}
