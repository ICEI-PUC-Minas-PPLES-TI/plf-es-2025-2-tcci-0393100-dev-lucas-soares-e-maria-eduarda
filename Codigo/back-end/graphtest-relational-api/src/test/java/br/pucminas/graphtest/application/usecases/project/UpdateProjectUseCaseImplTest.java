package br.pucminas.graphtest.application.usecases.project;

import br.pucminas.graphtest.application.domain.Project;
import br.pucminas.graphtest.application.port.input.project.records.ProjectOutput;
import br.pucminas.graphtest.application.port.input.project.records.UpdateProjectInput;
import br.pucminas.graphtest.application.port.output.repositories.ProjectRepository;
import br.pucminas.graphtest.application.service.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProjectUseCaseImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectAccessService projectAccessService;

    @InjectMocks
    private UpdateProjectUseCaseImpl useCase;

    @Test
    void shouldKeepCurrentNameWhenUpdateDoesNotProvideOne() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Project project = new Project(projectId, "Projeto Atual", "Descricao Atual", userId);
        UpdateProjectInput input = new UpdateProjectInput(projectId, null, "Nova Descricao");

        when(projectAccessService.findAuthorizedProject(projectId)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectOutput output = useCase.execute(input);

        assertEquals("Projeto Atual", output.name());
        assertEquals("Nova Descricao", output.description());
        verify(projectRepository).save(project);
    }

    @Test
    void shouldUpdateNameWhenUpdateProvidesOne() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Project project = new Project(projectId, "Projeto Atual", "Descricao Atual", userId);
        UpdateProjectInput input = new UpdateProjectInput(projectId, "Projeto Novo", "Nova Descricao");

        when(projectAccessService.findAuthorizedProject(projectId)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectOutput output = useCase.execute(input);

        assertEquals("Projeto Novo", output.name());
        assertEquals("Nova Descricao", output.description());
        verify(projectRepository).save(project);
    }

    @Test
    void shouldKeepCurrentDescriptionWhenUpdateDoesNotProvideOne() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Project project = new Project(projectId, "Projeto Atual", "Descricao Atual", userId);
        UpdateProjectInput input = new UpdateProjectInput(projectId, "Projeto Novo", null);

        when(projectAccessService.findAuthorizedProject(projectId)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectOutput output = useCase.execute(input);

        assertEquals("Projeto Novo", output.name());
        assertEquals("Descricao Atual", output.description());
        verify(projectRepository).save(project);
    }
}
