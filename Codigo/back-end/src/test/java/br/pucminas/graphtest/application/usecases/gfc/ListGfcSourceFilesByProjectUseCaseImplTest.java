package br.pucminas.graphtest.application.usecases.gfc;

import br.pucminas.graphtest.application.domain.gfc.model.GfcSourceFile;
import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.port.input.gfc.records.GfcSourceFileOutput;
import br.pucminas.graphtest.application.port.output.repositories.GfcSourceFileRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListGfcSourceFilesByProjectUseCaseImplTest {

    @Mock
    private GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort;

    @Mock
    private ProjectAccessService projectAccessService;

    @InjectMocks
    private ListGfcSourceFilesByProjectUseCaseImpl useCase;

    @Test
    void shouldListSourceFilesByProject() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        GfcSourceFile sourceFile = new GfcSourceFile(UUID.randomUUID(), projectId, "Exemplo.java", "class Exemplo {}", "Java");
        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", userId));
        when(gfcSourceFileRepositoryPort.findAllByProjectId(projectId)).thenReturn(List.of(sourceFile));

        List<GfcSourceFileOutput> outputs = useCase.execute(projectId);

        verify(projectAccessService).findAuthorizedProject(projectId);
        verify(gfcSourceFileRepositoryPort).findAllByProjectId(projectId);
        assertEquals(1, outputs.size());
        assertEquals(sourceFile.getId(), outputs.getFirst().id());
        assertEquals("Exemplo.java", outputs.getFirst().fileName());
    }

    @Test
    void shouldReturnEmptyListWhenProjectHasNoSourceFiles() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", userId));
        when(gfcSourceFileRepositoryPort.findAllByProjectId(projectId)).thenReturn(List.of());

        List<GfcSourceFileOutput> outputs = useCase.execute(projectId);

        verify(projectAccessService).findAuthorizedProject(projectId);
        assertEquals(0, outputs.size());
    }
}
