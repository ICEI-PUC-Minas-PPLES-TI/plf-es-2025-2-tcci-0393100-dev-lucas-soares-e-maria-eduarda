package br.pucminas.graphtest.application.service.decisiontable;

import br.pucminas.graphtest.application.domain.decisiontable.model.DecisionTable;
import br.pucminas.graphtest.application.domain.gce.enums.GceEdgeTypeEnum;
import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceEdge;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DecisionTableSyncServiceImplTest {

    private final DecisionTableSyncServiceImpl service = new DecisionTableSyncServiceImpl();
    private final DecisionTableDerivationServiceImpl derivationService = new DecisionTableDerivationServiceImpl();

    @Test
    void shouldThrowWhenDecisionTableIsNull() {
        assertThrows(NullPointerException.class, () -> service.isStale(null, graph("C1")));
    }

    @Test
    void shouldBeStaleWhenGraphIsNull() {
        DecisionTable table = derivationService.derive(graph("C1"), null);

        assertTrue(service.isStale(table, null));
    }

    @Test
    void shouldNotBeStaleWhenFingerprintMatchesCurrentGraph() {
        Gce graph = graph("C1");
        DecisionTable table = derivationService.derive(graph, null);

        assertFalse(service.isStale(table, graph));
    }

    @Test
    void shouldBeStaleWhenGraphChangedAfterDerivation() {
        Gce graph = graph("C1");
        DecisionTable table = derivationService.derive(graph, null);

        Gce changedGraph = graph("C2");
        assertTrue(service.isStale(table, changedGraph));
    }

    private Gce graph(String causeCode) {
        return new Gce(
                UUID.randomUUID(),
                UUID.randomUUID(),
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
