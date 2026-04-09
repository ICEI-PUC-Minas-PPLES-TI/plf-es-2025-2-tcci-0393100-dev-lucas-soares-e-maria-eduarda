package br.pucminas.graphtest.application.usecases.user;

import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import br.pucminas.graphtest.application.domain.user.model.User;
import br.pucminas.graphtest.application.port.input.user.records.DeleteUserInput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepositoryPort;
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

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteUserUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private UserAuthorizationService userAuthorizationService;

    @Mock
    private ProjectRepositoryPort projectRepository;

    @Mock
    private GceRepositoryPort gceRepository;

    @InjectMocks
    private DeleteUserUseCaseImpl useCase;

    @Test
    void shouldDeleteProjectGcesBeforeDeletingUserProjectsAndUser() {
        UUID userId = UUID.randomUUID();
        UUID projectIdOne = UUID.randomUUID();
        UUID projectIdTwo = UUID.randomUUID();
        User user = new User(userId, "Usuario de Teste", "teste@teste.com", "senha", UserProfileEnum.USUARIO);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(projectRepository.findAllByUserId(userId)).thenReturn(List.of(
                new Project(projectIdOne, "Projeto 1", "Descricao 1", userId),
                new Project(projectIdTwo, "Projeto 2", "Descricao 2", userId)
        ));

        useCase.execute(new DeleteUserInput(userId));

        InOrder inOrder = inOrder(userAuthorizationService, userRepository, projectRepository, gceRepository);
        inOrder.verify(userAuthorizationService).authorizeForUser(userId);
        inOrder.verify(userRepository).findById(userId);
        inOrder.verify(projectRepository).findAllByUserId(userId);
        inOrder.verify(gceRepository).deleteAllByProjectId(projectIdOne);
        inOrder.verify(gceRepository).deleteAllByProjectId(projectIdTwo);
        inOrder.verify(projectRepository).deleteAllByUserId(userId);
        inOrder.verify(userRepository).deleteById(userId);

        verify(projectRepository).deleteAllByUserId(userId);
        verify(userRepository).deleteById(userId);
    }
}
