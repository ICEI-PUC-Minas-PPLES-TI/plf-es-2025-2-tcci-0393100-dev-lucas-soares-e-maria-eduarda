package br.pucminas.graphtest.adapters.outbound.entities.neo4j.shared;

import br.pucminas.graphtest.adapters.outbound.entities.neo4j.gfc.Neo4jGfcEdgeRelationship;
import br.pucminas.graphtest.adapters.outbound.entities.neo4j.gfc.Neo4jGfcEntity;
import br.pucminas.graphtest.adapters.outbound.entities.neo4j.gfc.Neo4jGfcNodeEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Neo4jAuditTest {

    @Test
    void shouldFillCreatedAtAndUpdatedAtWhenCreatingGraphAggregate() {
        Neo4jGfcEntity graph = new Neo4jGfcEntity();
        Neo4jGfcNodeEntity sourceNode = new Neo4jGfcNodeEntity();
        Neo4jGfcEdgeRelationship edge = new Neo4jGfcEdgeRelationship();
        sourceNode.setOutgoingEdges(List.of(edge));
        graph.setNodes(List.of(sourceNode));

        graph.prepareAuditForSave();

        assertCreatedAndUpdatedTogether(graph);
        assertCreatedAndUpdatedTogether(sourceNode);
        assertNotNull(edge.getCreatedAt());
        assertNotNull(edge.getUpdatedAt());
        assertEquals(edge.getCreatedAt(), edge.getUpdatedAt());
    }

    @Test
    void shouldPreserveCreatedAtAndRefreshUpdatedAtWhenUpdatingGraphAggregate() {
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime previousUpdatedAt = createdAt.plusHours(1);
        Neo4jGfcEntity graph = new Neo4jGfcEntity();
        graph.setCreatedAt(createdAt);
        graph.setUpdatedAt(previousUpdatedAt);

        graph.prepareAuditForSave();

        assertEquals(createdAt, graph.getCreatedAt());
        assertTrue(graph.getUpdatedAt().isAfter(previousUpdatedAt));
    }

    private void assertCreatedAndUpdatedTogether(Neo4jBaseEntity entity) {
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
        assertEquals(entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
