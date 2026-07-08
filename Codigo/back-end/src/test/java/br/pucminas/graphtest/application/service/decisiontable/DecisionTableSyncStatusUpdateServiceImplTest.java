package br.pucminas.graphtest.application.service.decisiontable;

import br.pucminas.graphtest.application.domain.decisiontable.enums.DecisionTableSyncStatusEnum;
import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceEdge;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import br.pucminas.graphtest.application.port.output.repositories.DecisionTableRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DecisionTableSyncStatusUpdateServiceImplTest {

    @Mock
    private DecisionTableRepositoryPort decisionTableRepository;

    @InjectMocks
    private DecisionTableSyncStatusUpdateServiceImpl service;

    @Test
    void shouldMarkAsStaleAndSaveWhenTableIsUpToDate() {
        UUID gceId = UUID.randomUUID();
        DecisionTable table = decisionTable(DecisionTableSyncStatusEnum.UP_TO_DATE);
        when(decisionTableRepository.findByGceId(gceId)).thenReturn(Optional.of(table));

        service.markDecisionTableAsStaleByGceId(gceId);

        assertTrue(table.isStale());
        verify(decisionTableRepository).save(table);
    }

    @Test
    void shouldNotSaveWhenTableIsAlreadyStale() {
        UUID gceId = UUID.randomUUID();
        DecisionTable table = decisionTable(DecisionTableSyncStatusEnum.STALE);
        when(decisionTableRepository.findByGceId(gceId)).thenReturn(Optional.of(table));

        service.markDecisionTableAsStaleByGceId(gceId);

        verify(decisionTableRepository, never()).save(table);
    }

    @Test
    void shouldDoNothingWhenNoDecisionTableIsLinkedToGce() {
        UUID gceId = UUID.randomUUID();
        when(decisionTableRepository.findByGceId(gceId)).thenReturn(Optional.empty());

        service.markDecisionTableAsStaleByGceId(gceId);

        verify(decisionTableRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldThrowWhenPreviousGraphIsNull() {
        assertThrows(NullPointerException.class, () -> service.hasDecisionTableRelevantChanges(null, graph("C1")));
    }

    @Test
    void shouldThrowWhenCurrentGraphIsNull() {
        assertThrows(NullPointerException.class, () -> service.hasDecisionTableRelevantChanges(graph("C1"), null));
    }

    @Test
    void shouldDetectNoRelevantChangesWhenGraphsAreEquivalent() {
        UUID projectId = UUID.randomUUID();
        Gce previous = graph("C1", projectId);
        Gce current = graph("C1", projectId);

        assertFalse(service.hasDecisionTableRelevantChanges(previous, current));
    }

    @Test
    void shouldDetectRelevantChangesWhenNodeLabelDiffers() {
        UUID projectId = UUID.randomUUID();
        UUID graphId = UUID.randomUUID();
        Gce previous = new Gce(
                graphId, projectId, "GCE", null, false,
                List.of(GceNode.cause(UUID.randomUUID(), "C1", "Causa antiga"), GceNode.effect(UUID.randomUUID(), "E1", "Efeito")),
                List.of(new GceEdge(UUID.randomUUID(), "C1", "E1", GceEdgeTypeEnum.IDENTITY)),
                List.of()
        );
        Gce current = new Gce(
                graphId, projectId, "GCE", null, false,
                List.of(GceNode.cause(UUID.randomUUID(), "C1", "Causa nova"), GceNode.effect(UUID.randomUUID(), "E1", "Efeito")),
                List.of(new GceEdge(UUID.randomUUID(), "C1", "E1", GceEdgeTypeEnum.IDENTITY)),
                List.of()
        );

        assertTrue(service.hasDecisionTableRelevantChanges(previous, current));
    }

    private DecisionTable decisionTable(DecisionTableSyncStatusEnum syncStatus) {
        return new DecisionTable(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Tabela",
                null,
                "fingerprint",
                syncStatus,
                null,
                List.of(),
                List.of()
        );
    }

    private Gce graph(String causeCode) {
        return graph(causeCode, UUID.randomUUID());
    }

    private Gce graph(String causeCode, UUID projectId) {
        return new Gce(
                UUID.randomUUID(),
                projectId,
                "GCE",
                null,
                false,
                List.of(
                        GceNode.cause(UUID.randomUUID(), causeCode, "Causa"),
                        GceNode.effect(UUID.randomUUID(), "E1", "Efeito")
                ),
                List.of(new GceEdge(UUID.randomUUID(), causeCode, "E1", GceEdgeTypeEnum.IDENTITY)),
                List.of()
        );
    }
}
