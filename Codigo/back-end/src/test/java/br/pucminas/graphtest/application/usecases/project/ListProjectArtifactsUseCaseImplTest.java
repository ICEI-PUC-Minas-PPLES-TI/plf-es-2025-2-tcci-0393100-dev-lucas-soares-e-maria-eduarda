package br.pucminas.graphtest.application.usecases.project;

import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gfc.model.Gfc;
import br.pucminas.graphtest.application.domain.gfc.model.GfcSourceFile;
import br.pucminas.graphtest.application.domain.project.model.Project;
import br.pucminas.graphtest.application.port.input.project.records.ProjectArtifactOutput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GfcRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GfcSourceFileRepositoryPort;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListProjectArtifactsUseCaseImplTest {

    @Mock
    private ProjectAccessService projectAccessService;

    @Mock
    private GceRepositoryPort gceRepositoryPort;

    @Mock
    private GfcRepositoryPort gfcRepositoryPort;

    @Mock
    private DecisionTableRepositoryPort decisionTableRepositoryPort;

    @Mock
    private GfcSourceFileRepositoryPort gfcSourceFileRepositoryPort;

    @InjectMocks
    private ListProjectArtifactsUseCaseImpl useCase;

    @Test
    void shouldAuthorizeProjectListAllArtifactTypesWithRelationsAndSortByMostRecentDate() {
        UUID projectId = UUID.randomUUID();
        UUID gceId = UUID.randomUUID();
        UUID gfcId = UUID.randomUUID();
        UUID tableId = UUID.randomUUID();
        UUID sourceFileId = UUID.randomUUID();
        LocalDateTime oldest = LocalDateTime.of(2026, 5, 29, 10, 0);
        LocalDateTime middle = LocalDateTime.of(2026, 5, 30, 10, 0);
        LocalDateTime newest = LocalDateTime.of(2026, 5, 31, 12, 0);

        Gce gce = mock(Gce.class);
        when(gce.getId()).thenReturn(gceId);
        when(gce.getName()).thenReturn("GCE Login");
        when(gce.getCreatedAt()).thenReturn(oldest);
        when(gce.getUpdatedAt()).thenReturn(null);

        Gfc gfc = mock(Gfc.class);
        when(gfc.getId()).thenReturn(gfcId);
        when(gfc.getName()).thenReturn("GFC Login");
        when(gfc.getSourceFileId()).thenReturn(sourceFileId);
        when(gfc.getCreatedAt()).thenReturn(middle);
        when(gfc.getUpdatedAt()).thenReturn(null);

        GfcSourceFile sourceFile = mock(GfcSourceFile.class);
        when(sourceFile.getId()).thenReturn(sourceFileId);
        when(sourceFile.getFileName()).thenReturn("Login.java");

        DecisionTable decisionTable = mock(DecisionTable.class);
        when(decisionTable.getId()).thenReturn(tableId);
        when(decisionTable.getName()).thenReturn("Tabela Login");
        when(decisionTable.getGceId()).thenReturn(gceId);
        when(decisionTable.getCreatedAt()).thenReturn(oldest);
        when(decisionTable.getUpdatedAt()).thenReturn(newest);

        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", UUID.randomUUID()));
        when(gceRepositoryPort.findAllByProjectId(projectId)).thenReturn(List.of(gce));
        when(gfcRepositoryPort.findAllByProjectId(projectId)).thenReturn(List.of(gfc));
        when(decisionTableRepositoryPort.findAllByProjectId(projectId)).thenReturn(List.of(decisionTable));
        when(gfcSourceFileRepositoryPort.findAllByProjectId(projectId)).thenReturn(List.of(sourceFile));

        List<ProjectArtifactOutput> output = useCase.execute(projectId);

        verify(projectAccessService).findAuthorizedProject(projectId);
        verify(gceRepositoryPort).findAllByProjectId(projectId);
        verify(gfcRepositoryPort).findAllByProjectId(projectId);
        verify(decisionTableRepositoryPort).findAllByProjectId(projectId);
        verify(gfcSourceFileRepositoryPort).findAllByProjectId(projectId);

        assertEquals(3, output.size());
        assertEquals("DECISION_TABLE", output.get(0).type());
        assertEquals(tableId, output.get(0).id());
        assertEquals("GCE", output.get(0).relatedArtifact().type());
        assertEquals(gceId, output.get(0).relatedArtifact().id());
        assertEquals("GCE Login", output.get(0).relatedArtifact().name());

        assertEquals("GFC", output.get(1).type());
        assertEquals(gfcId, output.get(1).id());
        assertEquals("GFC_SOURCE_FILE", output.get(1).relatedArtifact().type());
        assertEquals(sourceFileId, output.get(1).relatedArtifact().id());
        assertEquals("Login.java", output.get(1).relatedArtifact().name());

        assertEquals("GCE", output.get(2).type());
        assertEquals(gceId, output.get(2).id());
        assertNull(output.get(2).relatedArtifact());
    }

    @Test
    void shouldKeepListingWhenRelatedArtifactsAreMissing() {
        UUID projectId = UUID.randomUUID();
        UUID missingGceId = UUID.randomUUID();
        UUID missingSourceFileId = UUID.randomUUID();

        Gfc gfc = mock(Gfc.class);
        when(gfc.getId()).thenReturn(UUID.randomUUID());
        when(gfc.getName()).thenReturn("GFC sem arquivo");
        when(gfc.getSourceFileId()).thenReturn(missingSourceFileId);

        DecisionTable decisionTable = mock(DecisionTable.class);
        when(decisionTable.getId()).thenReturn(UUID.randomUUID());
        when(decisionTable.getName()).thenReturn("Tabela sem GCE");
        when(decisionTable.getGceId()).thenReturn(missingGceId);

        when(projectAccessService.findAuthorizedProject(projectId))
                .thenReturn(new Project(projectId, "Projeto", "Descricao", UUID.randomUUID()));
        when(gceRepositoryPort.findAllByProjectId(projectId)).thenReturn(List.of());
        when(gfcRepositoryPort.findAllByProjectId(projectId)).thenReturn(List.of(gfc));
        when(decisionTableRepositoryPort.findAllByProjectId(projectId)).thenReturn(List.of(decisionTable));
        when(gfcSourceFileRepositoryPort.findAllByProjectId(projectId)).thenReturn(List.of());

        List<ProjectArtifactOutput> output = useCase.execute(projectId);

        assertEquals(2, output.size());
        ProjectArtifactOutput gfcOutput = output.stream()
                .filter(artifact -> artifact.type().equals("GFC"))
                .findFirst()
                .orElseThrow();
        assertEquals(missingSourceFileId, gfcOutput.relatedArtifact().id());
        assertNull(gfcOutput.relatedArtifact().name());

        ProjectArtifactOutput decisionTableOutput = output.stream()
                .filter(artifact -> artifact.type().equals("DECISION_TABLE"))
                .findFirst()
                .orElseThrow();
        assertEquals(missingGceId, decisionTableOutput.relatedArtifact().id());
        assertNull(decisionTableOutput.relatedArtifact().name());
    }
}
