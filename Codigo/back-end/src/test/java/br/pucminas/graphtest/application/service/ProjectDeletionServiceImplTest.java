package br.pucminas.graphtest.application.service;

import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GfcRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GfcSourceFileRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepositoryPort;
import br.pucminas.graphtest.application.service.project.ProjectDeletionServiceImpl;
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
class ProjectDeletionServiceImplTest {

    @Mock
    private ProjectRepositoryPort projectRepository;

    @Mock
    private GceRepositoryPort gceRepository;

    @Mock
    private DecisionTableRepositoryPort decisionTableRepository;

    @Mock
    private GfcRepositoryPort gfcRepository;

    @Mock
    private GfcSourceFileRepositoryPort gfcSourceFileRepository;

    @InjectMocks
    private ProjectDeletionServiceImpl service;

    @Test
    void shouldDeleteGfcsSourceFilesGcesDecisionTablesAndProject() {
        UUID projectId = UUID.randomUUID();
        Project project = new Project(projectId, "Projeto", "Descricao", UUID.randomUUID());

        service.deleteProject(project);

        InOrder inOrder = inOrder(gfcRepository, gfcSourceFileRepository, gceRepository, decisionTableRepository, projectRepository);
        inOrder.verify(gfcRepository).deleteAllByProjectId(projectId);
        inOrder.verify(gfcSourceFileRepository).deleteAllByProjectId(projectId);
        inOrder.verify(gceRepository).deleteAllByProjectId(projectId);
        inOrder.verify(decisionTableRepository).deleteAllByProjectId(projectId);
        inOrder.verify(projectRepository).deleteById(projectId);
    }

    @Test
    void shouldDeleteGfcsSourceFilesGcesAndDecisionTablesForEveryUserProjectBeforeDeletingProjects() {
        UUID userId = UUID.randomUUID();
        Project firstProject = new Project(UUID.randomUUID(), "Projeto 1", "Descricao", userId);
        Project secondProject = new Project(UUID.randomUUID(), "Projeto 2", "Descricao", userId);
        when(projectRepository.findAllByUserId(userId)).thenReturn(List.of(firstProject, secondProject));

        service.deleteProjectsByUserId(userId);

        verify(gfcRepository).deleteAllByProjectId(firstProject.getId());
        verify(gfcSourceFileRepository).deleteAllByProjectId(firstProject.getId());
        verify(gceRepository).deleteAllByProjectId(firstProject.getId());
        verify(decisionTableRepository).deleteAllByProjectId(firstProject.getId());
        verify(gfcRepository).deleteAllByProjectId(secondProject.getId());
        verify(gfcSourceFileRepository).deleteAllByProjectId(secondProject.getId());
        verify(gceRepository).deleteAllByProjectId(secondProject.getId());
        verify(decisionTableRepository).deleteAllByProjectId(secondProject.getId());
        verify(projectRepository).deleteAllByUserId(userId);
    }
}
