package br.pucminas.graphtest.application.usecases.project;

import br.pucminas.graphtest.application.domain.Project;
import br.pucminas.graphtest.application.port.input.project.records.DeleteProjectInput;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepositoryPort;
import br.pucminas.graphtest.application.service.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteProjectUseCaseImplTest {

    @Mock
    private ProjectRepositoryPort projectRepository;

    @Mock
    private GceRepositoryPort gceRepository;

    @Mock
    private ProjectAccessService projectAccessService;

    @InjectMocks
    private DeleteProjectUseCaseImpl useCase;

    @Test
    void shouldDeleteAllProjectGcesBeforeDeletingProject() {
        UUID projectId = UUID.randomUUID();
        Project project = new Project(projectId, "Projeto", "Descricao", UUID.randomUUID());

        when(projectAccessService.findAuthorizedProject(projectId)).thenReturn(project);

        useCase.execute(new DeleteProjectInput(projectId));

        InOrder inOrder = inOrder(projectAccessService, gceRepository, projectRepository);
        inOrder.verify(projectAccessService).findAuthorizedProject(projectId);
        inOrder.verify(gceRepository).deleteAllByProjectId(projectId);
        inOrder.verify(projectRepository).deleteById(projectId);
        verify(gceRepository).deleteAllByProjectId(projectId);
    }
}
