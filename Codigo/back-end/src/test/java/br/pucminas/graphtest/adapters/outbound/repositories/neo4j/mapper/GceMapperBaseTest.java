package br.pucminas.graphtest.adapters.outbound.repositories.neo4j.mapper;

import br.pucminas.graphtest.adapters.outbound.entities.neo4j.gce.Neo4jGceEntity;
import br.pucminas.graphtest.adapters.outbound.entities.neo4j.gce.Neo4jGceNodeEntity;
import br.pucminas.graphtest.application.domain.gce.model.Gce;
import br.pucminas.graphtest.application.domain.gce.model.GceNode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GceMapperBaseTest {

    private final GceMapperBase mapper = new GceMapperBase();

    @Test
    void shouldAssignGraphScopedCodeToEveryPersistedNode() {
        Gce graph = new Gce(
                null,
                UUID.randomUUID(),
                "GCE",
                "Descricao",
                false,
                List.of(
                        GceNode.cause(UUID.randomUUID(), "C1", "Causa 1"),
                        GceNode.effect(UUID.randomUUID(), "E1", "Efeito 1")
                ),
                List.of(),
                List.of()
        );

        Neo4jGceEntity entity = mapper.toEntity(graph);

        assertNotNull(entity.getId());
        assertEquals(2, entity.getNodes().size());
        for (Neo4jGceNodeEntity nodeEntity : entity.getNodes()) {
            assertEquals(entity.getId() + ":" + nodeEntity.getCode(), nodeEntity.getGraphScopedCode());
        }
    }
}
