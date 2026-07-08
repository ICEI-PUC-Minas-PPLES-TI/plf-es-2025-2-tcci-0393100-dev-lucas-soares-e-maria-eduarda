package br.pucminas.graphtest.application.usecases.project;

import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.domain.user.enums.UserProfileEnum;
import br.pucminas.graphtest.application.port.input.project.records.ProjectOutput;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepositoryPort;
import br.pucminas.graphtest.application.port.output.security.CurrentUserPort;
import br.pucminas.graphtest.application.security.AuthenticatedUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListProjectsByUserUseCaseImplTest {

    @Mock
    private ProjectRepositoryPort projectRepository;

    @Mock
    private CurrentUserPort currentUserPort;

    @InjectMocks
    private ListProjectsByUserUseCaseImpl useCase;

    @Test
    void shouldListProjectsOwnedByCurrentUser() {
        UUID userId = UUID.randomUUID();
        when(currentUserPort.getCurrentUser())
                .thenReturn(new AuthenticatedUser(userId, "usuario", UserProfileEnum.USUARIO));
        Project project = new Project(UUID.randomUUID(), "Projeto", "Descricao", userId);
        when(projectRepository.findAllByUserId(userId)).thenReturn(List.of(project));

        List<ProjectOutput> outputs = useCase.execute();

        assertEquals(1, outputs.size());
        assertEquals(project.getId(), outputs.getFirst().id());
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoProjects() {
        UUID userId = UUID.randomUUID();
        when(currentUserPort.getCurrentUser())
                .thenReturn(new AuthenticatedUser(userId, "usuario", UserProfileEnum.USUARIO));
        when(projectRepository.findAllByUserId(userId)).thenReturn(List.of());

        assertEquals(0, useCase.execute().size());
    }
}
