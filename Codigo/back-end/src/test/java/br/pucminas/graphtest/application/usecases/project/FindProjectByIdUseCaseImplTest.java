package br.pucminas.graphtest.application.usecases.project;

import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.project.records.FindProjectByIdInput;
import br.pucminas.graphtest.application.port.input.project.records.ProjectOutput;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindProjectByIdUseCaseImplTest {

    @Mock
    private ProjectAccessService projectAccessService;

    @InjectMocks
    private FindProjectByIdUseCaseImpl useCase;

    @Test
    void shouldReturnAuthorizedProject() {
        UUID projectId = UUID.randomUUID();
        Project project = new Project(projectId, "Projeto", "Descricao", UUID.randomUUID());
        when(projectAccessService.findAuthorizedProject(projectId)).thenReturn(project);

        ProjectOutput output = useCase.execute(new FindProjectByIdInput(projectId));

        assertEquals(projectId, output.id());
    }

    @Test
    void shouldPropagateNotFoundFromAccessService() {
        UUID projectId = UUID.randomUUID();
        when(projectAccessService.findAuthorizedProject(projectId))
                .thenThrow(new EntityNotFoundException("Projeto nao encontrado"));

        assertThrows(EntityNotFoundException.class, () -> useCase.execute(new FindProjectByIdInput(projectId)));
    }
}
