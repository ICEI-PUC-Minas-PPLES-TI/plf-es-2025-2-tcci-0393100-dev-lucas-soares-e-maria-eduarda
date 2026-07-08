package br.pucminas.graphtest.application.usecases.decisiontable;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableSyncStatusEnum;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceEdge;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import br.pucminas.graphtest.application.exception.EntityNotFoundException;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableByGceIdInput;
import br.pucminas.graphtest.application.port.input.decisiontable.records.DecisionTableOutput;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import br.pucminas.graphtest.application.port.output.repositories.GceRepositoryPort;
import br.pucminas.graphtest.application.service.decisiontable.interfaces.DecisionTableDerivationService;
import br.pucminas.graphtest.application.service.gce.interfaces.GceMutationService;
import br.pucminas.graphtest.application.service.project.interfaces.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PreviewDecisionTableUseCaseImplTest {

    private static final UUID GCE_ID = UUID.randomUUID();
    private static final UUID PROJECT_ID = UUID.randomUUID();

    @Mock
    private DecisionTableRepositoryPort decisionTableRepository;

    @Mock
    private GceRepositoryPort gceRepository;

    @Mock
    private ProjectAccessService projectAccessService;

    @Mock
    private GceMutationService gceMutationService;

    @Mock
    private DecisionTableDerivationService decisionTableDerivationService;

    @InjectMocks
    private PreviewDecisionTableUseCaseImpl useCase;

    @Test
    void shouldDerivePreviewReusingExistingDecisionTable() {
        Gce graph = graph(PROJECT_ID);
        DecisionTable currentTable = decisionTable(PROJECT_ID);
        DecisionTable previewTable = decisionTable(PROJECT_ID);
        when(gceMutationService.loadAuthorizedGraph(GCE_ID, gceRepository, projectAccessService)).thenReturn(graph);
        when(decisionTableRepository.findByGceId(GCE_ID)).thenReturn(Optional.of(currentTable));
        when(decisionTableDerivationService.derive(graph, currentTable)).thenReturn(previewTable);

        DecisionTableOutput output = useCase.execute(new DecisionTableByGceIdInput(PROJECT_ID, GCE_ID));

        assertEquals(previewTable.getId(), output.id());
        verify(decisionTableDerivationService).derive(graph, currentTable);
    }

    @Test
    void shouldDerivePreviewWithoutCurrentTableWhenNoneExists() {
        Gce graph = graph(PROJECT_ID);
        DecisionTable previewTable = decisionTable(PROJECT_ID);
        when(gceMutationService.loadAuthorizedGraph(GCE_ID, gceRepository, projectAccessService)).thenReturn(graph);
        when(decisionTableRepository.findByGceId(GCE_ID)).thenReturn(Optional.empty());
        when(decisionTableDerivationService.derive(graph, null)).thenReturn(previewTable);

        DecisionTableOutput output = useCase.execute(new DecisionTableByGceIdInput(PROJECT_ID, GCE_ID));

        assertEquals(previewTable.getId(), output.id());
    }

    @Test
    void shouldThrowNotFoundWhenGraphDoesNotBelongToProject() {
        Gce graph = graph(UUID.randomUUID());
        when(gceMutationService.loadAuthorizedGraph(GCE_ID, gceRepository, projectAccessService)).thenReturn(graph);

        assertThrows(EntityNotFoundException.class,
                () -> useCase.execute(new DecisionTableByGceIdInput(PROJECT_ID, GCE_ID)));
        verifyNoInteractions(decisionTableDerivationService, decisionTableRepository);
    }

    private Gce graph(UUID projectId) {
        return new Gce(
                GCE_ID,
                projectId,
                "GCE",
                null,
                false,
                List.of(GceNode.cause(UUID.randomUUID(), "C1", "Causa"), GceNode.effect(UUID.randomUUID(), "E1", "Efeito")),
                List.of(new GceEdge(UUID.randomUUID(), "C1", "E1", GceEdgeTypeEnum.IDENTITY)),
                List.of()
        );
    }

    private DecisionTable decisionTable(UUID projectId) {
        return new DecisionTable(
                UUID.randomUUID(),
                GCE_ID,
                projectId,
                "Tabela",
                null,
                "fingerprint",
                DecisionTableSyncStatusEnum.UP_TO_DATE,
                null,
                List.of(),
                List.of()
        );
    }
}
