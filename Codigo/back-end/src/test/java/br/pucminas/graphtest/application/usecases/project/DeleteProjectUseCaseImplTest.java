package br.pucminas.graphtest.application.usecases.project;

import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.port.input.project.records.DeleteProjectInput;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectDeletionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteProjectUseCaseImplTest {

    @Mock
    private ProjectAccessService projectAccessService;

    @Mock
    private ProjectDeletionService projectDeletionService;

    @InjectMocks
    private DeleteProjectUseCaseImpl useCase;

    @Test
    void shouldDeleteProjectThroughCentralizedDeletionService() {
        UUID projectId = UUID.randomUUID();
        Project project = new Project(projectId, "Projeto", "Descricao", UUID.randomUUID());

        when(projectAccessService.findAuthorizedProject(projectId)).thenReturn(project);

        useCase.execute(new DeleteProjectInput(projectId));

        verify(projectAccessService).findAuthorizedProject(projectId);
        verify(projectDeletionService).deleteProject(project);
    }
}
