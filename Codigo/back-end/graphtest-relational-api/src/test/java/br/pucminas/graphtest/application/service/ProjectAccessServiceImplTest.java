package br.pucminas.graphtest.application.service;

import br.pucminas.graphtest.application.domain.Project;
import br.pucminas.graphtest.application.domain.enums.UserProfileEnum;
import br.pucminas.graphtest.application.domain.records.AuthenticatedUser;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepositoryPort;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectAccessServiceImplTest {

    @Mock
    private ProjectRepositoryPort projectRepository;

    @Mock
    private CurrentUserPort currentUserPort;

    @InjectMocks
    private ProjectAccessServiceImpl service;

    @Test
    void shouldReturnProjectWhenCurrentUserIsAdmin() {
        UUID projectId = UUID.randomUUID();
        Project project = new Project(projectId, "Projeto", "Descricao", UUID.randomUUID());

        when(currentUserPort.getCurrentUser())
                .thenReturn(new AuthenticatedUser(UUID.randomUUID(), "admin", UserProfileEnum.ADMIN));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        Project result = service.findAuthorizedProject(projectId);

        assertSame(project, result);
        verify(projectRepository).findById(projectId);
        verify(projectRepository, never()).findByIdAndUserId(projectId, project.getUserId());
    }

    @Test
    void shouldReturnProjectWhenCurrentUserOwnsProject() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        Project project = new Project(projectId, "Projeto", "Descricao", userId);

        when(currentUserPort.getCurrentUser())
                .thenReturn(new AuthenticatedUser(userId, "usuario", UserProfileEnum.USUARIO));
        when(projectRepository.findByIdAndUserId(projectId, userId)).thenReturn(Optional.of(project));

        Project result = service.findAuthorizedProject(projectId);

        assertSame(project, result);
        verify(projectRepository).findByIdAndUserId(projectId, userId);
        verify(projectRepository, never()).findById(projectId);
    }

    @Test
    void shouldThrowWhenCurrentUserHasNoAccessToProject() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        when(currentUserPort.getCurrentUser())
                .thenReturn(new AuthenticatedUser(userId, "usuario", UserProfileEnum.USUARIO));
        when(projectRepository.findByIdAndUserId(projectId, userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.findAuthorizedProject(projectId));

        verify(projectRepository).findByIdAndUserId(projectId, userId);
        verify(projectRepository, never()).findById(projectId);
    }
}
